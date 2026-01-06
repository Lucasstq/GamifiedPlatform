package dev.gamified.GamifiedPlatform.services.mission;

import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.domain.Mission;
import dev.gamified.GamifiedPlatform.dtos.request.mission.MissionCreateRequest;
import dev.gamified.GamifiedPlatform.dtos.response.MissionResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourseNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.MissionMapper;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import dev.gamified.GamifiedPlatform.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateMissionService {

    private final MissionRepository missionRepository;
    private final LevelRepository levelRepository;

    @Transactional
    public MissionResponse execute(MissionCreateRequest request) {
        log.info("Criando nova missão para o nível {}", request.levelId());

        Levels level = levelRepository.findById(request.levelId())
                .orElseThrow(() -> new ResourseNotFoundException("Nível não encontrado com ID: " + request.levelId()));

        validateOrderNumber(level.getId(), request.orderNumber());

        Mission mission = MissionMapper.toEntity(request, level);

        Mission savedMission = missionRepository.save(mission);
        log.info("Missão criada com sucesso: {}", savedMission.getId());

        return MissionMapper.toResponse(savedMission);
    }

    // Verificar se já existe missão com essa ordem no nível
    private void validateOrderNumber(Long levelId, Integer orderNumber) {
        if (missionRepository.existsByLevelIdAndOrderNumber(levelId, orderNumber)) {
            throw new IllegalArgumentException("Já existe uma missão com ordem " + orderNumber + " neste nível");
        }
    }
}
