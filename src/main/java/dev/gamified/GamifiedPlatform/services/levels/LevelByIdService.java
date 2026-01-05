package dev.gamified.GamifiedPlatform.services.levels;

import dev.gamified.GamifiedPlatform.dtos.response.LevelResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourseNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.LevelMapper;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LevelByIdService {

    private final LevelRepository levelRepository;

    /**
     * Busca um nível específico por ID
     */
    public LevelResponse execute(Long id) {
        return levelRepository.findById(id)
                .map(LevelMapper::toResponse)
                .orElseThrow(() -> new ResourseNotFoundException("Level not found with id: " + id));
    }
}
