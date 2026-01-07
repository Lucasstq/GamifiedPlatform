package dev.gamified.GamifiedPlatform.controller.level;

import dev.gamified.GamifiedPlatform.config.annotations.CanReadLevels;
import dev.gamified.GamifiedPlatform.dtos.response.LevelResponse;
import dev.gamified.GamifiedPlatform.enums.DifficultyLevel;
import dev.gamified.GamifiedPlatform.services.levels.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * Controller responsável pelos endpoints relacionados as consultas aos níveis de gamificação.
 */
@RestController
@RequestMapping("/levels")
@RequiredArgsConstructor
public class LevelQueryController {

    private final GetAllLevelsService getAllLevels;
    private final LevelByIdService getLevelById;
    private final LevelByOrderService getLevelByOrder;
    private final GetLevelByDifficultyService getLevelByDifficulty;
    private final LevelByUserAuthenticate getLevelByAuthenticatedUser;
    private final GetNextLevelService getNextLevel;
    private final GetUnlockLevelsService getUnlockLevels;
    private final GetLockedLevelsService getLockedLevels;
    private final GetSystemStatsService getSystemStats;

    /**
     * GET /levels - Listar todos os níveis paginados
     */
    @GetMapping
    @CanReadLevels
    public ResponseEntity<Page<LevelResponse>> getAllLevels(
            @PageableDefault(size = 20, sort = "orderLevel") Pageable pageable
    ) {
        return ResponseEntity.ok(getAllLevels.execute(pageable));
    }

    /**
     * GET /levels/{id} - Buscar nível por ID
     */
    @GetMapping("/{id}")
    @CanReadLevels
    public ResponseEntity<LevelResponse> getLevelById(@PathVariable Long id) {
        LevelResponse level = getLevelById.execute(id);
        return ResponseEntity.ok(level);
    }

    /**
     * GET /levels/order/{orderLevel} - Buscar nível por número de ordem
     */
    @GetMapping("/order/{orderLevel}")
    @CanReadLevels
    public ResponseEntity<LevelResponse> getLevelByOrder(@PathVariable Integer orderLevel) {
        LevelResponse level = getLevelByOrder.execute(orderLevel);
        return ResponseEntity.ok(level);
    }

    /**
     * GET /levels/difficulty/{difficulty} - Buscar níveis por dificuldade
     */
    @GetMapping("/difficulty/{difficulty}")
    @CanReadLevels
    public ResponseEntity<List<LevelResponse>> getLevelsByDifficulty(@PathVariable DifficultyLevel difficulty) {
        List<LevelResponse> levels = getLevelByDifficulty.execute(difficulty);
        return ResponseEntity.ok(levels);
    }

    /**
     * GET /levels/user/{userId} - Buscar nível atual do usuário baseado em seu XP
     */
    @GetMapping("/user/{userId}")
    @CanReadLevels
    public ResponseEntity<LevelResponse> getUserLevel(@PathVariable Long userId) {
        LevelResponse level = getLevelByAuthenticatedUser.execute(userId);
        return ResponseEntity.ok(level);
    }

    // Endpoints de Funcionalidades de Gamificação

    /**
     * GET /levels/next/{currentOrderLevel} - Buscar próximo nível na progressão
     */
    @GetMapping("/next/{currentOrderLevel}")
    @CanReadLevels
    public ResponseEntity<LevelResponse> getNextLevel(@PathVariable Integer currentOrderLevel) {
        LevelResponse nextLevel = getNextLevel.execute(currentOrderLevel);
        return ResponseEntity.ok(nextLevel);
    }

    /**
     * GET /levels/unlocked?currentXp={xp} - Listar níveis desbloqueados
     */
    @GetMapping("/unlocked")
    @CanReadLevels
    public ResponseEntity<List<LevelResponse>> getUnlockedLevels(@RequestParam Integer currentXp) {
        List<LevelResponse> unlockedLevels = getUnlockLevels.execute(currentXp);
        return ResponseEntity.ok(unlockedLevels);
    }

    /**
     * GET /levels/locked?currentXp={xp} - Listar níveis ainda bloqueados
     */
    @GetMapping("/locked")
    @CanReadLevels
    public ResponseEntity<List<LevelResponse>> getLockedLevels(@RequestParam Integer currentXp) {
        List<LevelResponse> lockedLevels = getLockedLevels.execute(currentXp);
        return ResponseEntity.ok(lockedLevels);
    }

    /**
     * GET /levels/stats - Obter estatísticas gerais do sistema de níveis
     */
    @GetMapping("/stats")
    @CanReadLevels
    public ResponseEntity<GetSystemStatsService.LevelSystemStats> getSystemStats() {
        GetSystemStatsService.LevelSystemStats stats = getSystemStats.execute();
        return ResponseEntity.ok(stats);
    }
}
