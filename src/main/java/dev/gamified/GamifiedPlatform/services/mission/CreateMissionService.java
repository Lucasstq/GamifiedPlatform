package dev.gamified.GamifiedPlatform.services.mission;

import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.domain.Mission;
import dev.gamified.GamifiedPlatform.dtos.request.mission.MissionCreateRequest;
import dev.gamified.GamifiedPlatform.dtos.response.MissionResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.MissionMapper;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import dev.gamified.GamifiedPlatform.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableCaching
public class CreateMissionService {

    private final MissionRepository missionRepository;
    private final LevelRepository levelRepository;

    /**
     * Cria uma nova missão e invalida o cache de missões daquele nível.
     */
    @CacheEvict(value = "missionsByLevel", key = "#request.levelId()")
    @Transactional
    public MissionResponse execute(MissionCreateRequest request) {
        log.info("Creating a new mission for the level {}", request.levelId());

        Levels level = levelRepository.findById(request.levelId())
                .orElseThrow(() -> new ResourceNotFoundException("Level not found by ID: " + request.levelId()));

        validateOrderNumber(level.getId(), request.orderNumber());

        Mission mission = MissionMapper.toEntity(request, level);

        Mission savedMission = missionRepository.save(mission);
        log.info("Mission create successful: {}", savedMission.getId());

        return MissionMapper.toResponse(savedMission);
    }

    // Verificar se já existe missão com essa ordem no nível
    private void validateOrderNumber(Long levelId, Integer orderNumber) {
        if (missionRepository.existsByLevelIdAndOrderNumber(levelId, orderNumber)) {
            throw new IllegalArgumentException("There is already a mission with the order " + orderNumber + " at this level");
        }
    }
}
