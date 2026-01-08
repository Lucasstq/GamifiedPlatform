package dev.gamified.GamifiedPlatform.services.mission;

import dev.gamified.GamifiedPlatform.dtos.response.missions.MissionResponse;
import dev.gamified.GamifiedPlatform.mapper.MissionMapper;
import dev.gamified.GamifiedPlatform.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAllMissionsService {

    private final MissionRepository missionRepository;

    @Transactional(readOnly = true)
    public Page<MissionResponse> execute(Pageable pageable) {
        log.info("Buscando todas as missões - página: {}, tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return missionRepository.findAll(pageable)
                .map(MissionMapper::toResponse);
    }
}
