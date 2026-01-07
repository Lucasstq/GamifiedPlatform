package dev.gamified.GamifiedPlatform.services.levels;

import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.dtos.response.LevelResponse;
import dev.gamified.GamifiedPlatform.mapper.LevelMapper;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableCaching
public class GetAllLevelsService {

    private final LevelRepository levelRepository;

    /*
     * Lista todos os níveis paginados ordenados por ordem crescente paginados
     */
    @Cacheable(value = "levels", key = "'all'")
    public Page<LevelResponse> execute(Pageable pageable) {
        Page<Levels> levels = levelRepository.findAllByOrderByOrderLevelAsc(pageable);
        return levels.map(LevelMapper::toResponse);
    }

    /**
     * Método sem paginação para casos específicos
     * Cacheia o resultado pois níveis são praticamente imutáveis
     */
    @Cacheable(value = "levels", key = "'all'")
    public List<LevelResponse> executeAll() {
        return levelRepository.findAllByOrderByOrderLevelAsc()
                .stream()
                .map(LevelMapper::toResponse)
                .collect(Collectors.toList());
    }
}
