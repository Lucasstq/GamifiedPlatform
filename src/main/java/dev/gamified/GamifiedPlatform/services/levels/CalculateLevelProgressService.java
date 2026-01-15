package dev.gamified.GamifiedPlatform.services.levels;

import dev.gamified.GamifiedPlatform.constants.BusinessConstants;
import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.dtos.response.levels.LevelResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Current level not found"));

        try {
            LevelResponse nextLevel = getNextLevel.execute(currentOrderLevel);
            int xpInCurrentLevel = currentXp - currentLevel.getXpRequired();
            int xpNeededForNextLevel = nextLevel.xpRequired() - currentLevel.getXpRequired();

            if (xpNeededForNextLevel <= 0) return BusinessConstants.MAX_PROGRESS_PERCENTAGE;

            double progress = (double) xpInCurrentLevel / xpNeededForNextLevel * 100;
            return Math.min(BusinessConstants.MAX_PROGRESS_PERCENTAGE,
                    Math.max(BusinessConstants.MIN_PROGRESS_PERCENTAGE, progress));
        } catch (ResourceNotFoundException e) {
            // Está no nível máximo
            return BusinessConstants.MAX_PROGRESS_PERCENTAGE;
        }
    }

}
