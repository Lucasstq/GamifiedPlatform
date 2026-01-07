package dev.gamified.GamifiedPlatform.services.levels;

import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CanUnlockLevelService {

    private final LevelRepository levelRepository;

    /**
     * Verifica se o jogador tem XP suficiente para desbloquear um nível específico
     */
    public boolean execute(Integer currentXp, Long levelId) {
        Levels level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found with id: " + levelId));
        return currentXp >= level.getXpRequired();
    }

}
