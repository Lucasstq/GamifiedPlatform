package dev.gamified.GamifiedPlatform.services.levels;

import dev.gamified.GamifiedPlatform.dtos.response.LevelResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourseNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalculateXpToNextLevelService {

    private final GetNextLevelService getNextLevel;

    /**
     * Calcula quantos XP faltam para o próximo nível
     */
    public Integer execute(Integer currentXp, Integer currentOrderLevel) {
        try {
            LevelResponse nextLevel = getNextLevel.execute(currentOrderLevel);
            int xpNeeded = nextLevel.xpRequired() - currentXp;
            return Math.max(0, xpNeeded); // Garante que não retorna valor negativo
        } catch (ResourseNotFoundException e) {
            return 0; // Já está no nível máximo
        }
    }

}
