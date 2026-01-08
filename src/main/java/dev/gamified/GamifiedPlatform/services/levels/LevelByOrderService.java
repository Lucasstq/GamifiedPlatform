package dev.gamified.GamifiedPlatform.services.levels;

import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.dtos.response.levels.LevelResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.LevelMapper;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LevelByOrderService {

    private final LevelRepository levelRepository;

    /**
     * Busca um nível pelo seu número de ordem
     */
    public LevelResponse execute(Integer orderLevel) {
        Levels level = levelRepository.findByOrderLevel(orderLevel)
                .orElseThrow(() -> new ResourceNotFoundException("Level with order " + orderLevel + " not found"));
        return LevelMapper.toResponse(level);
    }

}
