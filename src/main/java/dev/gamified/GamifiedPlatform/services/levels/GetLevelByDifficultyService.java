package dev.gamified.GamifiedPlatform.services.levels;

import dev.gamified.GamifiedPlatform.dtos.response.levels.LevelResponse;
import dev.gamified.GamifiedPlatform.enums.DifficultyLevel;
import dev.gamified.GamifiedPlatform.mapper.LevelMapper;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetLevelByDifficultyService {

    private final LevelRepository levelRepository;

    /**
     * Busca níveis por dificuldade (EASY, MEDIUM, HARD, EXPERT) paginados
     */
    public Page<LevelResponse> execute(DifficultyLevel difficulty, Pageable pageable) {
        List<LevelResponse> allLevels = levelRepository.findByDifficultyLevel(difficulty)
                .stream()
                .map(LevelMapper::toResponse)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allLevels.size());

        List<LevelResponse> pageContent = allLevels.subList(start, end);
        return new PageImpl<>(pageContent, pageable, allLevels.size());
    }

}
