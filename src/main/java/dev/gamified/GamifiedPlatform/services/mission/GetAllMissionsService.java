package dev.gamified.GamifiedPlatform.services.mission;

import dev.gamified.GamifiedPlatform.dtos.response.MissionResponse;
import dev.gamified.GamifiedPlatform.mapper.MissionMapper;
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
public class GetAllMissionsService {

    private final MissionRepository missionRepository;

    @Transactional(readOnly = true)
    public List<MissionResponse> execute() {
        log.info("Buscando todas as missões");
        return missionRepository.findAll().stream()
                .map(MissionMapper::toResponse)
                .collect(Collectors.toList());
    }
}
