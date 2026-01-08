package dev.gamified.GamifiedPlatform.services.mission;

import dev.gamified.GamifiedPlatform.domain.Mission;
import dev.gamified.GamifiedPlatform.dtos.response.missions.MissionResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.MissionMapper;
import dev.gamified.GamifiedPlatform.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableCaching
public class GetMissionById {

    private final MissionRepository missionRepository;

    /**
     * Busca uma missão específica por ID.
     * Cacheia o resultado pois missões mudam pouco frequentemente.
     */
    @Cacheable(value = "missions", key = "#missionId")
    @Transactional(readOnly = true)
    public MissionResponse execute(Long missionId) {
        log.info("Looking for missions at ID {}", missionId);
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found for ID: " + missionId));
        return MissionMapper.toResponse(mission);
    }
}
