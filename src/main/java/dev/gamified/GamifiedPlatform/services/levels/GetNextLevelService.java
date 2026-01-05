package dev.gamified.GamifiedPlatform.services.levels;

import dev.gamified.GamifiedPlatform.dtos.response.LevelResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourseNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.LevelMapper;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetNextLevelService {

    private final LevelRepository levelRepository;

    /**
     * Retorna o próximo nível na progressão
     */
    public LevelResponse execute(Integer currentOrderLevel) {
        return levelRepository.findByOrderLevel(currentOrderLevel + 1)
                .map(LevelMapper::toResponse)
                .orElseThrow(() -> new ResourseNotFoundException("No next level found. You've reached the maximum level!"));
    }

}
