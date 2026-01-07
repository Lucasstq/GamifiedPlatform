package dev.gamified.GamifiedPlatform.services.mission;

import dev.gamified.GamifiedPlatform.dtos.response.MissionResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.MissionMapper;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import dev.gamified.GamifiedPlatform.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
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
     * Busca todas as missões de um nível específico.
     * Cacheia o resultado pois missões mudam pouco frequentemente.
     */
    @Cacheable(value = "missionsByLevel", key = "#levelId")
    @Transactional(readOnly = true)
    public List<MissionResponse> execute(Long levelId) {
        log.info("Looking for missions at level {}", levelId);
        levelRepository.findById(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found for ID: " + levelId));

        return missionRepository.findByLevelIdOrderByOrderNumberAsc(levelId).stream()
                .map(MissionMapper::toResponse)
                .toList();
    }
}
