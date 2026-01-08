package dev.gamified.GamifiedPlatform.controller.level;

import dev.gamified.GamifiedPlatform.config.annotations.CanReadLevels;
import dev.gamified.GamifiedPlatform.dtos.response.levels.LevelResponse;
import dev.gamified.GamifiedPlatform.services.levels.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pelos endpoints relacionados aos níveis de gamificação.
 */
@RestController
@RequestMapping("/levels")
@RequiredArgsConstructor
public class LevelController {

    private final CalculateLevelByXpService calculateLevelByXp;
    private final CalculateXpToNextLevelService calculateXpToNextLevel;
    private final CanUnlockLevelService canUnlockLevel;
    private final CalculateLevelProgressService calculateLevelProgress;

    // Endpoints de Funcionalidades de Gamificação

    /**
     * GET /levels/calculate-by-xp?xp={xp} - Calcular qual nível corresponde ao XP informado
     */
    @GetMapping("/calculate-by-xp")
    @CanReadLevels
    public ResponseEntity<LevelResponse> calculateLevelByXp(@RequestParam Integer xp) {
        LevelResponse level = calculateLevelByXp.execute(xp);
        return ResponseEntity.ok(level);
    }

    /**
     * GET /levels/xp-to-next?currentXp={xp}&currentOrderLevel={order}
     * Calcular XP necessário para próximo nível
     */
    @GetMapping("/xp-to-next")
    @CanReadLevels
    public ResponseEntity<Integer> calculateXpToNextLevel(
            @RequestParam Integer currentXp,
            @RequestParam Integer currentOrderLevel) {
        Integer xpNeeded = calculateXpToNextLevel.execute(currentXp, currentOrderLevel);
        return ResponseEntity.ok(xpNeeded);
    }

    /**
     * GET /levels/can-unlock?currentXp={xp}&levelId={id}
     * Verificar se o jogador pode desbloquear um nível específico
     */
    @GetMapping("/can-unlock")
    @CanReadLevels
    public ResponseEntity<Boolean> canUnlockLevel(
            @RequestParam Integer currentXp,
            @RequestParam Long levelId) {
        boolean canUnlock = canUnlockLevel.execute(currentXp, levelId);
        return ResponseEntity.ok(canUnlock);
    }


    /**
     * GET /levels/progress?currentXp={xp}&currentOrderLevel={order}
     * Calcular progresso percentual no nível atual
     */
    @GetMapping("/progress")
    @CanReadLevels
    public ResponseEntity<Double> calculateLevelProgress(
            @RequestParam Integer currentXp,
            @RequestParam Integer currentOrderLevel) {
        Double progress = calculateLevelProgress.execute(currentXp, currentOrderLevel);
        return ResponseEntity.ok(progress);
    }
}

