package dev.gamified.GamifiedPlatform.services.ranking;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.response.ranking.RankingResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import dev.gamified.GamifiedPlatform.repository.PlayerCharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j

public class GetRankingByLevelService {

    private static final String RANKING_BY_LEVEL_PREFIX = "ranking_by_level:";
    private final PlayerCharacterRepository playerCharacterRepository;
    private final LevelRepository levelRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RefreshRankingCacheService refreshRankingCache;

    @Transactional
    public Page<RankingResponse> execute(Long levelId, Pageable pageable) {

        log.info("Fetching ranking for level: {} - page: {}, size: {}",
                levelId, pageable.getPageNumber(), pageable.getPageSize());

        Levels level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found with id: " + levelId));

        String levelKey = RANKING_BY_LEVEL_PREFIX + level.getOrderLevel();
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();

        Long cacheSize = zSetOps.size(levelKey);
        if (cacheSize == null || cacheSize == 0) {
            refreshRankingCache.execute();
            cacheSize = zSetOps.size(levelKey);
        }

        long start = pageable.getOffset();
        long end = start + pageable.getPageSize() - 1;

        Set<Object> characterIds = zSetOps.reverseRange(levelKey, start, end);
        if (characterIds == null || characterIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, cacheSize != null ? cacheSize : 0);
        }

        //Converte IDs para List<Long> mantendo a ordem do Redis
        List<Long> charIds = characterIds.stream()
                .map(id -> ((Number) id).longValue())
                .toList();

        //Reaproveita o mesmo método do global ranking
        List<PlayerCharacter> characters = playerCharacterRepository.findAllByIdInWithUser(charIds);

        //Mapa por ID para manter a ordem do Redis
        Map<Long, PlayerCharacter> characterMap = characters.stream()
                .collect(Collectors.toMap(PlayerCharacter::getId, c -> c));

        Set<Integer> levelOrders = characters.stream()
                .map(PlayerCharacter::getLevel)
                .collect(Collectors.toSet());

        Map<Integer, Levels> levelsMap = levelRepository.findAllByOrderLevelIn(levelOrders)
                .stream()
                .collect(Collectors.toMap(Levels::getOrderLevel, l -> l));

        List<RankingResponse> ranking = new ArrayList<>();
        int position = (pageable.getPageNumber() * pageable.getPageSize()) + 1;
        Long currentUserId = SecurityUtils.getCurrentUserId().orElse(null);

        for (Long charId : charIds) {
            PlayerCharacter character = characterMap.get(charId);
            if (character == null) continue;

            User user = character.getUser();
            Levels charLevel = levelsMap.get(character.getLevel());

            ranking.add(RankingResponse.builder()
                    .position(position)
                    .userId(user.getId())
                    .username(user.getUsername())
                    .characterName(character.getName())
                    .level(character.getLevel())
                    .xp(character.getXp())
                    .levelName(charLevel != null ? charLevel.getName() : "Unknown")
                    .levelTitle(charLevel != null ? charLevel.getTitle() : "Unknown")
                    .isMe(currentUserId != null && currentUserId.equals(user.getId()))
                    .build());
            position++;
        }
        return new PageImpl<>(ranking, pageable, cacheSize != null ? cacheSize : 0);
    }
}
