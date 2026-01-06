package dev.gamified.GamifiedPlatform.services.mission;

import dev.gamified.GamifiedPlatform.dtos.response.MissionResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourseNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.MissionMapper;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import dev.gamified.GamifiedPlatform.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetMissionByLevelService {

    private final MissionRepository missionRepository;
    private final LevelRepository levelRepository;

    @Transactional(readOnly = true)
    public List<MissionResponse> execute(Long levelId) {
        log.info("Buscando missões do nível {}", levelId);
        levelRepository.findById(levelId)
                .orElseThrow(() -> new ResourseNotFoundException("Nível não encontrado com ID: " + levelId));

        return missionRepository.findByLevelIdOrderByOrderNumberAsc(levelId).stream()
                .map(MissionMapper::toResponse)
                .collect(Collectors.toList());
    }
}
