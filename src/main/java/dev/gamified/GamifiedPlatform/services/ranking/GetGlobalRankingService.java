package dev.gamified.GamifiedPlatform.services.ranking;
import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.response.ranking.RankingResponse;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j

public class GetGlobalRankingService {

    private static final String RANKING_CACHE_KEY = "global_ranking";
    private final PlayerCharacterRepository playerCharacterRepository;
    private final LevelRepository levelRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RefreshRankingCacheService refreshRankingCache;

    @Transactional(readOnly = true)
    public Page<RankingResponse> execute(Pageable pageable) {
        log.info("Fetching global ranking - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        Long cacheSize = zSetOps.size(RANKING_CACHE_KEY);

        if (cacheSize == null || cacheSize == 0) {
            refreshRankingCache.execute();
            cacheSize = zSetOps.size(RANKING_CACHE_KEY);
        }

        long start = pageable.getOffset();
        long end = start + pageable.getPageSize() - 1;

        Set<Object> characterIds = zSetOps.reverseRange(RANKING_CACHE_KEY, start, end);

        if (characterIds == null || characterIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, cacheSize != null ? cacheSize : 0);
        }

        //Converte IDs para List<Long>
        List<Long> charIds = characterIds.stream()
                .map(id -> ((Number) id).longValue())
                .collect(Collectors.toList());

        List<PlayerCharacter> characters = playerCharacterRepository.findAllByIdInWithUser(charIds);

        //Cria mapa de characters por ID para manter a ordem do ranking
        Map<Long, PlayerCharacter> characterMap = characters.stream()
                .collect(Collectors.toMap(PlayerCharacter::getId, c -> c));

        //Coleta todos os níveis únicos necessários
        Set<Integer> levelOrders = characters.stream()
                .map(PlayerCharacter::getLevel)
                .collect(Collectors.toSet());

        //Busca todos os níveis em uma única query
        Map<Integer, Levels> levelsMap = levelRepository.findAllByOrderLevelIn(levelOrders)
                .stream()
                .collect(Collectors.toMap(Levels::getOrderLevel, l -> l));

        //Monta a resposta mantendo a ordem do ranking
        List<RankingResponse> ranking = new ArrayList<>();
        int position = (pageable.getPageNumber() * pageable.getPageSize()) + 1;
        Long currentUserId = SecurityUtils.getCurrentUserId().orElse(null);

        for (Long charId : charIds) {
            PlayerCharacter character = characterMap.get(charId);

            if (character != null) {
                User user = character.getUser();
                Levels level = levelsMap.get(character.getLevel());

                ranking.add(RankingResponse.builder()
                        .position(position)
                        .userId(user.getId())
                        .username(user.getUsername())
                        .characterName(character.getName())
                        .level(character.getLevel())
                        .xp(character.getXp())
                        .levelName(level != null ? level.getName() : "Unknown")
                        .levelTitle(level != null ? level.getTitle() : "Unknown")
                        .isMe(currentUserId != null && currentUserId.equals(user.getId()))
                        .build());
                position++;
            }
        }

        log.debug("Ranking fetched with {} entries using optimized batch queries", ranking.size());
        return new PageImpl<>(ranking, pageable, cacheSize != null ? cacheSize : 0);
    }
}
