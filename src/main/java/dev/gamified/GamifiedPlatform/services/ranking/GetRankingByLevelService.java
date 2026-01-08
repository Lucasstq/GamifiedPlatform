package dev.gamified.GamifiedPlatform.services.ranking;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.response.RankingResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import dev.gamified.GamifiedPlatform.repository.PlayerCharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    public List<RankingResponse> execute(Long levelId, int page, int size) {

        log.info("Fetching ranking for level: {} - page: {}, size: {}", levelId, page, size);

        Levels level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found with id: " + levelId));

        String levelKey = RANKING_BY_LEVEL_PREFIX + level.getOrderLevel();
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();

        Long cacheSize = zSetOps.size(levelKey);
        if (cacheSize == null || cacheSize == 0) {
            refreshRankingCache.execute();
        }

        long start = (long) page * size;
        long end = start + size - 1;

        Set<Object> characterIds = zSetOps.reverseRange(levelKey, start, end);
        if (characterIds == null || characterIds.isEmpty()) {
            return List.of();
        }

        List<RankingResponse> ranking = new ArrayList<>();
        int position = (page * size) + 1;
        Long currentUserId = SecurityUtils.getCurrentUserId().orElse(null);

        for (Object characterId : characterIds) {
            Long charId = ((Number) characterId).longValue();
            PlayerCharacter character = playerCharacterRepository.findById(charId).orElse(null);

            if (character != null) {

                User user = character.getUser();
                Levels charLevel = levelRepository.findTopByOrderLevelLessThanEqualOrderByOrderLevelDesc(character.getLevel())
                        .orElse(null);

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
        }

        return ranking;
    }
}
