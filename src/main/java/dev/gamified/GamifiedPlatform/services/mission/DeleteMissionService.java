package dev.gamified.GamifiedPlatform.services.mission;

import dev.gamified.GamifiedPlatform.domain.Mission;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableCaching
public class DeleteMissionService {

    private final MissionRepository missionRepository;

    /**
     * Deleta uma missão e invalida o cache relacionado.
     * Remove tanto do cache de missões individuais quanto do cache de missões por nível.
     */
    @Transactional
    public void execute(Long missionId) {
        log.info("Delete mission {}", missionId);

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found by ID: " + missionId));

        Long levelId = mission.getLevel().getId();

        missionRepository.deleteById(missionId);

        // Invalida os caches após deletar
        evictCaches(missionId, levelId);

        log.info("Mission delete successful: {}", missionId);
    }

    @Caching(evict = {
            @CacheEvict(value = "missions", key = "#missionId"),
            @CacheEvict(value = "missionsByLevel", key = "#levelId")
    })
    public void evictCaches(Long missionId, Long levelId) {
        log.debug("Cache invalidated for mission {} and level {}", missionId, levelId);
    }
}
