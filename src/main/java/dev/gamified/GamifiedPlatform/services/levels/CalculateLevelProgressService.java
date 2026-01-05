package dev.gamified.GamifiedPlatform.services.levels;

import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.dtos.response.LevelResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourseNotFoundException;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalculateLevelProgressService {

    private final GetNextLevelService getNextLevel;
    private final LevelRepository levelRepository;

    /**
     * Calcula o progresso percentual no nível atual
     */
    public Double execute(Integer currentXp, Integer currentOrderLevel) {
        Levels currentLevel = levelRepository.findByOrderLevel(currentOrderLevel)
                .orElseThrow(() -> new ResourseNotFoundException("Current level not found"));

        try {
            LevelResponse nextLevel = getNextLevel.execute(currentOrderLevel);
            int xpInCurrentLevel = currentXp - currentLevel.getXpRequired();
            int xpNeededForNextLevel = nextLevel.xpRequired() - currentLevel.getXpRequired();

            if (xpNeededForNextLevel <= 0) return 100.0;

            double progress = (double) xpInCurrentLevel / xpNeededForNextLevel * 100;
            return Math.min(100.0, Math.max(0.0, progress));
        } catch (ResourseNotFoundException e) {
            // Está no nível máximo
            return 100.0;
        }
    }

}
