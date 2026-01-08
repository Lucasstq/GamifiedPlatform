package dev.gamified.GamifiedPlatform.services.mission;

import dev.gamified.GamifiedPlatform.dtos.response.missions.MissionResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.MissionMapper;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import dev.gamified.GamifiedPlatform.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableCaching
public class GetMissionByLevelService {

    private final MissionRepository missionRepository;
    private final LevelRepository levelRepository;

    /**
     * Busca todas as missões de um nível específico paginadas.
     * Cacheia o resultado pois missões mudam pouco frequentemente.
     */
    @Cacheable(value = "missionsByLevel", key = "#levelId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    @Transactional(readOnly = true)
    public Page<MissionResponse> execute(Long levelId, Pageable pageable) {
        log.info("Looking for missions at level {} - página: {}, tamanho: {}",
                levelId, pageable.getPageNumber(), pageable.getPageSize());
        levelRepository.findById(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found for ID: " + levelId));

        List<MissionResponse> allMissions = missionRepository.findByLevelIdOrderByOrderNumberAsc(levelId).stream()
                .map(MissionMapper::toResponse)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allMissions.size());

        List<MissionResponse> pageContent = allMissions.subList(start, end);
        return new PageImpl<>(pageContent, pageable, allMissions.size());
    }
}
