package dev.gamified.GamifiedPlatform.services.ranking;
import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.repository.PlayerCharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableCaching
public class RefreshRankingCacheService {

    private static final String RANKING_CACHE_KEY = "global_ranking";
    private static final String RANKING_BY_LEVEL_PREFIX = "ranking_by_level:";
    private final PlayerCharacterRepository playerCharacterRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Scheduled(fixedRate = 300000)
    @CacheEvict(value = {"ranking", "rankingByLevel"}, allEntries = true)
    public void execute() {
        log.info("Refreshing ranking cache...");

        try {
            redisTemplate.delete(RANKING_CACHE_KEY);
            Sort sort = Sort.by(Sort.Direction.DESC, "level", "xp");
            List<PlayerCharacter> allCharacters = playerCharacterRepository.findAll(sort);
            ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();

            for (PlayerCharacter character : allCharacters) {
                double score = (character.getLevel() * 1_000_000.0) + character.getXp();
                zSetOps.add(RANKING_CACHE_KEY, character.getId(), score);
            }

            for (int level = 1; level <= 10; level++) {
                String levelKey = RANKING_BY_LEVEL_PREFIX + level;
                redisTemplate.delete(levelKey);
                int finalLevel = level;
                allCharacters.stream()
                        .filter(c -> c.getLevel().equals(finalLevel))
                        .forEach(c -> zSetOps.add(levelKey, c.getId(), c.getXp()));
            }

            log.info("Ranking cache refreshed successfully. Total players: {}", allCharacters.size());
        } catch (Exception e) {
            log.error("Error refreshing ranking cache", e);
        }
    }
}
