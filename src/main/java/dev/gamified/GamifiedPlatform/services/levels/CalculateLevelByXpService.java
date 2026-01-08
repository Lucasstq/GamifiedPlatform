package dev.gamified.GamifiedPlatform.services.levels;

import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.dtos.response.levels.LevelResponse;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.mapper.LevelMapper;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculateLevelByXpService {

    private final LevelRepository levelRepository;

    /**
     * Determina qual nível um personagem deve estar baseado em seu XP atual
     */
    public LevelResponse execute(Integer currentXp) {
        List<Levels> allLevels = levelRepository.findAllByOrderByOrderLevelAsc();

        if (allLevels.isEmpty()) {
            throw new BusinessException("No levels configured in the system");
        }

        // Encontra o nível mais alto que o jogador pode alcançar com o XP atual
        Levels achievedLevel = allLevels.get(0); // Começa com o primeiro nível

        for (Levels level : allLevels) {
            if (currentXp >= level.getXpRequired()) {
                achievedLevel = level;
            } else {
                break; // Para quando encontrar um nível que não pode ser alcançado
            }
        }

        return LevelMapper.toResponse(achievedLevel);
    }
}
