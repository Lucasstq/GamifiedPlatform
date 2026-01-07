package dev.gamified.GamifiedPlatform.services.levels;

import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.dtos.response.LevelResponse;
import dev.gamified.GamifiedPlatform.enums.DifficultyLevel;
import dev.gamified.GamifiedPlatform.mapper.LevelMapper;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@EnableCaching
public class GetSystemStatsService {

    private final LevelRepository levelRepository;

    /**
     * Retorna estatísticas gerais sobre os níveis do sistema
     * Cacheia o resultado pois as estatísticas mudam raramente
     */
    @Cacheable(value = "levels", key = "'stats'")
    public LevelSystemStats execute() {
        List<Levels> allLevels = levelRepository.findAll();

        if (allLevels.isEmpty()) {
            return new LevelSystemStats(0, 0, 0, null, null);
        }

        int totalLevels = allLevels.size();
        int maxXpRequired = allLevels.stream()
                .mapToInt(Levels::getXpRequired)
                .max()
                .orElse(0);
        int minXpRequired = allLevels.stream()
                .mapToInt(Levels::getXpRequired)
                .min()
                .orElse(0);

        Levels easiestLevel = allLevels.stream()
                .filter(l -> l.getDifficultyLevel() == DifficultyLevel.EASY)
                .findFirst()
                .orElse(null);

        Levels hardestLevel = allLevels.stream()
                .filter(l -> l.getDifficultyLevel() == DifficultyLevel.EXPERT)
                .reduce((first, second) -> second) // Pega o último EXPERT
                .orElse(null);

        return new LevelSystemStats(
                totalLevels,
                minXpRequired,
                maxXpRequired,
                easiestLevel != null ? LevelMapper.toResponse(easiestLevel) : null,
                hardestLevel != null ? LevelMapper.toResponse(hardestLevel) : null
        );
    }

    /**
     * Classe para estatísticas do sistema de níveis
     */
    public record LevelSystemStats(
            int totalLevels,
            int minXpRequired,
            int maxXpRequired,
            LevelResponse easiestLevel,
            LevelResponse hardestLevel
    ) {
    }
}


