package dev.gamified.GamifiedPlatform.services.levels;

import dev.gamified.GamifiedPlatform.dtos.response.LevelResponse;
import dev.gamified.GamifiedPlatform.enums.DifficultyLevel;
import dev.gamified.GamifiedPlatform.mapper.LevelMapper;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetLevelByDifficultyService {

    private final LevelRepository levelRepository;

    /**
     * Busca níveis por dificuldade (EASY, MEDIUM, HARD, EXPERT)
     */
    public List<LevelResponse> execute(DifficultyLevel difficulty) {
        return levelRepository.findByDifficultyLevel(difficulty)
                .stream()
                .map(LevelMapper::toResponse)
                .collect(Collectors.toList());
    }

}
