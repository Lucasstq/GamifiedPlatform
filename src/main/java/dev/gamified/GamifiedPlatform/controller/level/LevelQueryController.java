package dev.gamified.GamifiedPlatform.controller.level;

import dev.gamified.GamifiedPlatform.config.annotations.CanReadLevels;
import dev.gamified.GamifiedPlatform.dtos.response.levels.LevelResponse;
import dev.gamified.GamifiedPlatform.enums.DifficultyLevel;
import dev.gamified.GamifiedPlatform.services.levels.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pelos endpoints relacionados as consultas aos níveis de gamificação.
 */
@RestController
@RequestMapping("/levels")
@RequiredArgsConstructor
@Tag(name = "Níveis", description = "Sistema de progressão e níveis de conhecimento")
@SecurityRequirement(name = "bearerAuth")
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

    @GetMapping
    @CanReadLevels
    @Operation(
            summary = "Listar todos os níveis",
            description = "Retorna uma lista paginada de todos os níveis disponíveis no sistema, ordenados por ordem de progressão"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de níveis retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<LevelResponse>> getAllLevels(
            @PageableDefault(size = 20, sort = "orderLevel") Pageable pageable
    ) {
        return ResponseEntity.ok(getAllLevels.execute(pageable));
    }

    @GetMapping("/{id}")
    @CanReadLevels
    @Operation(
            summary = "Buscar nível por ID",
            description = "Retorna os detalhes de um nível específico pelo seu ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nível encontrado",
                    content = @Content(schema = @Schema(implementation = LevelResponse.class))),
            @ApiResponse(responseCode = "404", description = "Nível não encontrado")
    })
    public ResponseEntity<LevelResponse> getLevelById(
            @Parameter(description = "ID do nível", required = true) @PathVariable Long id) {
        LevelResponse level = getLevelById.execute(id);
        return ResponseEntity.ok(level);
    }

    @GetMapping("/order/{orderLevel}")
    @CanReadLevels
    @Operation(
            summary = "Buscar nível por ordem",
            description = "Retorna um nível específico pelo seu número de ordem (1-10)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nível encontrado",
                    content = @Content(schema = @Schema(implementation = LevelResponse.class))),
            @ApiResponse(responseCode = "404", description = "Nível não encontrado")
    })
    public ResponseEntity<LevelResponse> getLevelByOrder(
            @Parameter(description = "Ordem do nível (1-10)", required = true) @PathVariable Integer orderLevel) {
        LevelResponse level = getLevelByOrder.execute(orderLevel);
        return ResponseEntity.ok(level);
    }

    @GetMapping("/difficulty/{difficulty}")
    @CanReadLevels
    @Operation(
            summary = "Buscar níveis por dificuldade",
            description = "Retorna uma lista paginada de níveis filtrados por dificuldade (BEGINNER, INTERMEDIATE, ADVANCED, EXPERT, MASTER)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de níveis retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<LevelResponse>> getLevelsByDifficulty(
            @Parameter(description = "Dificuldade do nível", required = true) @PathVariable DifficultyLevel difficulty,
            @PageableDefault(size = 20, sort = "orderLevel") Pageable pageable) {
        Page<LevelResponse> levels = getLevelByDifficulty.execute(difficulty, pageable);
        return ResponseEntity.ok(levels);
    }

    @GetMapping("/user/{userId}")
    @CanReadLevels
    @Operation(
            summary = "Buscar nível atual do usuário",
            description = "Retorna o nível atual de um usuário baseado em seu XP acumulado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nível do usuário encontrado",
                    content = @Content(schema = @Schema(implementation = LevelResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<LevelResponse> getUserLevel(
            @Parameter(description = "ID do usuário", required = true) @PathVariable Long userId) {
        LevelResponse level = getLevelByAuthenticatedUser.execute(userId);
        return ResponseEntity.ok(level);
    }

    @GetMapping("/next/{currentOrderLevel}")
    @CanReadLevels
    @Operation(
            summary = "Buscar próximo nível",
            description = "Retorna o próximo nível na progressão baseado no nível atual"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Próximo nível encontrado",
                    content = @Content(schema = @Schema(implementation = LevelResponse.class))),
            @ApiResponse(responseCode = "404", description = "Próximo nível não encontrado (já está no nível máximo)")
    })
    public ResponseEntity<LevelResponse> getNextLevel(
            @Parameter(description = "Ordem do nível atual", required = true) @PathVariable Integer currentOrderLevel) {
        LevelResponse nextLevel = getNextLevel.execute(currentOrderLevel);
        return ResponseEntity.ok(nextLevel);
    }

    @GetMapping("/unlocked")
    @CanReadLevels
    @Operation(
            summary = "Listar níveis desbloqueados",
            description = "Retorna uma lista paginada de níveis que o usuário já desbloqueou baseado no XP fornecido"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de níveis desbloqueados",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<LevelResponse>> getUnlockedLevels(
            @Parameter(description = "XP atual do usuário", required = true) @RequestParam Integer currentXp,
            @PageableDefault(size = 20, sort = "orderLevel") Pageable pageable) {
        Page<LevelResponse> unlockedLevels = getUnlockLevels.execute(currentXp, pageable);
        return ResponseEntity.ok(unlockedLevels);
    }

    @GetMapping("/locked")
    @CanReadLevels
    @Operation(
            summary = "Listar níveis bloqueados",
            description = "Retorna uma lista paginada de níveis que o usuário ainda não desbloqueou baseado no XP fornecido"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de níveis bloqueados",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<LevelResponse>> getLockedLevels(
            @Parameter(description = "XP atual do usuário", required = true) @RequestParam Integer currentXp,
            @PageableDefault(size = 20, sort = "orderLevel") Pageable pageable) {
        Page<LevelResponse> lockedLevels = getLockedLevels.execute(currentXp, pageable);
        return ResponseEntity.ok(lockedLevels);
    }

    @GetMapping("/stats")
    @CanReadLevels
    @Operation(
            summary = "Obter estatísticas do sistema de níveis",
            description = "Retorna estatísticas gerais sobre o sistema de níveis, incluindo total de níveis, distribuição por dificuldade, etc."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso",
                    content = @Content(schema = @Schema(implementation = GetSystemStatsService.LevelSystemStats.class)))
    })
    public ResponseEntity<GetSystemStatsService.LevelSystemStats> getSystemStats() {
        GetSystemStatsService.LevelSystemStats stats = getSystemStats.execute();
        return ResponseEntity.ok(stats);
    }
}
