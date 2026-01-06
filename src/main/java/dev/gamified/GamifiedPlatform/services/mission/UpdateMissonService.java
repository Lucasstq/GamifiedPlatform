package dev.gamified.GamifiedPlatform.services.mission;

import dev.gamified.GamifiedPlatform.domain.Mission;
import dev.gamified.GamifiedPlatform.dtos.request.mission.MissionUpdateRequest;
import dev.gamified.GamifiedPlatform.dtos.response.MissionResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourseNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.MissionMapper;
import dev.gamified.GamifiedPlatform.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateMissonService {

    private final MissionRepository missionRepository;

    @Transactional
    public MissionResponse execute(Long missionId, MissionUpdateRequest request) {
        log.info("Atualizando missão {}", missionId);

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourseNotFoundException("Missão não encontrada com ID: " + missionId));

        MissionMapper.updateEntity(mission, request);

        return MissionMapper.toResponse(mission);
    }
}
