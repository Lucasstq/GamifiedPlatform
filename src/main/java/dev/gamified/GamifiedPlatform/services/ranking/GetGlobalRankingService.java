package dev.gamified.GamifiedPlatform.services.ranking;
import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.response.RankingResponse;
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

public class GetGlobalRankingService {

    private static final String RANKING_CACHE_KEY = "global_ranking";
    private final PlayerCharacterRepository playerCharacterRepository;
    private final LevelRepository levelRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RefreshRankingCacheService refreshRankingCache;

    @Transactional
    public List<RankingResponse> execute(int page, int size) {
        log.info("Fetching global ranking - page: {}, size: {}", page, size);

        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        Long cacheSize = zSetOps.size(RANKING_CACHE_KEY);

        if (cacheSize == null || cacheSize == 0) {
            refreshRankingCache.execute();
        }

        long start = (long) page * size;
        long end = start + size - 1;

        Set<Object> characterIds = zSetOps.reverseRange(RANKING_CACHE_KEY, start, end);

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
                Levels level = levelRepository.findTopByOrderLevelLessThanEqualOrderByOrderLevelDesc(character.getLevel())
                        .orElse(null);

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
        return ranking;
    }
}
