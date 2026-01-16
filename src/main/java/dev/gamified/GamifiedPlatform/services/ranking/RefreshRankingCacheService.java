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
import java.util.UUID;

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
            Sort sort = Sort.by(Sort.Direction.DESC, "level", "xp");
            List<PlayerCharacter> allCharacters = playerCharacterRepository.findAll(sort);

            if (allCharacters.isEmpty()) {
                log.warn("No characters found to populate ranking cache");
                return;
            }

            // Atualiza ranking global
            updateGlobalRanking(allCharacters);

            // Atualiza ranking por nível
            updateRankingByLevel(allCharacters);

            log.info("Ranking cache refreshed successfully. Total players: {}", allCharacters.size());
        } catch (Exception e) {
            log.error("Error refreshing ranking cache", e);
        }
    }

    private void updateGlobalRanking(List<PlayerCharacter> allCharacters) {
        String tempKey = RANKING_CACHE_KEY + ":temp:" + UUID.randomUUID();
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();

        try {
            for (PlayerCharacter character : allCharacters) {
                double score = (character.getLevel() * 1_000_000.0) + character.getXp();
                zSetOps.add(tempKey, character.getId(), score);
            }

            // Verifica se a chave temporária tem dados antes de renomear
            Long size = zSetOps.size(tempKey);
            if (size != null && size > 0) {
                redisTemplate.rename(tempKey, RANKING_CACHE_KEY);
                log.debug("Global ranking updated with {} entries", size);
            } else {
                log.warn("Temporary global ranking key is empty, skipping rename");
            }
        } catch (Exception e) {
            // Limpa a chave temporária em caso de erro
            redisTemplate.delete(tempKey);
            throw e;
        }
    }

    private void updateRankingByLevel(List<PlayerCharacter> allCharacters) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();

        for (int level = 1; level <= 10; level++) {
            String levelKey = RANKING_BY_LEVEL_PREFIX + level;
            String tempLevelKey = levelKey + ":temp:" + UUID.randomUUID();

            try {
                int finalLevel = level;
                List<PlayerCharacter> charactersAtLevel = allCharacters.stream()
                        .filter(c -> c.getLevel().equals(finalLevel))
                        .toList();

                if (charactersAtLevel.isEmpty()) {
                    log.debug("No characters found for level {}, skipping", level);
                    // Remove a chave do nível se existir (caso não haja mais personagens)
                    redisTemplate.delete(levelKey);
                    continue;
                }

                // Adiciona os personagens do nível
                for (PlayerCharacter character : charactersAtLevel) {
                    zSetOps.add(tempLevelKey, character.getId(), character.getXp());
                }

                // Verifica se a chave temporária tem dados antes de renomear
                Long size = zSetOps.size(tempLevelKey);
                if (size != null && size > 0) {
                    redisTemplate.rename(tempLevelKey, levelKey);
                    log.debug("Ranking for level {} updated with {} entries", level, size);
                } else {
                    log.warn("Temporary ranking key for level {} is empty", level);
                }
            } catch (Exception e) {
                // Limpa a chave temporária em caso de erro
                redisTemplate.delete(tempLevelKey);
                log.error("Error updating ranking for level {}", level, e);
            }
        }
    }
}