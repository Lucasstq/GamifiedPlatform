package dev.gamified.GamifiedPlatform.services.mission;

import dev.gamified.GamifiedPlatform.domain.Mission;
import dev.gamified.GamifiedPlatform.dtos.request.mission.MissionUpdateRequest;
import dev.gamified.GamifiedPlatform.dtos.response.MissionResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.MissionMapper;
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
public class UpdateMissonService {

    private final MissionRepository missionRepository;

    /**
     * Atualiza uma missão e invalida o cache relacionado.
     * Remove tanto do cache de missões individuais quanto do cache de missões por nível.
     */
    @Caching(evict = {
            @CacheEvict(value = "missions", key = "#missionId"),
            @CacheEvict(value = "missionsByLevel", key = "#result.levelId")
    })
    @Transactional
    public MissionResponse execute(Long missionId, MissionUpdateRequest request) {
        log.info("Updating mission {}", missionId);

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found for ID: " + missionId));

        MissionMapper.updateEntity(mission, request);

        return MissionMapper.toResponse(mission);
    }
}
