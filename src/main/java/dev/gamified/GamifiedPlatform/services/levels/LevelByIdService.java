package dev.gamified.GamifiedPlatform.services.levels;

import dev.gamified.GamifiedPlatform.dtos.response.levels.LevelResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.LevelMapper;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableCaching
public class LevelByIdService {

    private final LevelRepository levelRepository;

    /**
     * Busca um nível específico por ID
     * Cacheia o resultado pois níveis mudam muito raramente
     */
    @Cacheable(value = "levels", key = "#id")
    public LevelResponse execute(Long id) {
        return levelRepository.findById(id)
                .map(LevelMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found with id: " + id));
    }
}
