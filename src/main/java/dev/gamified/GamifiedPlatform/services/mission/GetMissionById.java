package dev.gamified.GamifiedPlatform.services.mission;

import dev.gamified.GamifiedPlatform.domain.Mission;
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
public class GetMissionById {

    private final MissionRepository missionRepository;

    @Transactional(readOnly = true)
    public MissionResponse execute(Long missionId) {
        log.info("Buscando missão com ID {}", missionId);
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourseNotFoundException("Missão não encontrada com ID: " + missionId));
        return MissionMapper.toResponse(mission);
    }
}
