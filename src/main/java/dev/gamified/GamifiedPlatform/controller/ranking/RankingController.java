package dev.gamified.GamifiedPlatform.controller.ranking;

import dev.gamified.GamifiedPlatform.config.annotations.CanReadUsers;
import dev.gamified.GamifiedPlatform.config.annotations.IsAdmin;
import dev.gamified.GamifiedPlatform.dtos.response.ranking.MyRankingResponse;
import dev.gamified.GamifiedPlatform.dtos.response.ranking.RankingResponse;
import dev.gamified.GamifiedPlatform.services.ranking.GetGlobalRankingService;
import dev.gamified.GamifiedPlatform.services.ranking.GetMyRankingService;
import dev.gamified.GamifiedPlatform.services.ranking.GetRankingByLevelService;
import dev.gamified.GamifiedPlatform.services.ranking.RefreshRankingCacheService;
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
 * Controller para gerenciar o ranking global de jogadores.
 */
@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
@Tag(name = "Ranking", description = "Sistema de classificação global de jogadores")
@SecurityRequirement(name = "bearerAuth")
public class RankingController {

    private final GetGlobalRankingService getGlobalRankingService;
    private final GetMyRankingService getMyRankingService;
    private final GetRankingByLevelService getRankingByLevelService;
    private final RefreshRankingCacheService refreshRankingCacheService;

    @GetMapping
    @CanReadUsers
    @Operation(
            summary = "Buscar ranking global",
            description = "Retorna o ranking global paginado dos jogadores ordenado por XP total"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ranking retornado com sucesso",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<RankingResponse>> getGlobalRanking(
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(getGlobalRankingService.execute(pageable));
    }

    @GetMapping("/me")
    @CanReadUsers
    @Operation(
            summary = "Buscar minha posição no ranking",
            description = "Retorna a posição e informações do usuário autenticado no ranking global"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posição no ranking retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = MyRankingResponse.class)))
    })
    public ResponseEntity<MyRankingResponse> getMyRanking() {
        return ResponseEntity.ok(getMyRankingService.execute());
    }

    @GetMapping("/level/{levelId}")
    @CanReadUsers
    @Operation(
            summary = "Buscar ranking por nível",
            description = "Retorna o ranking paginado filtrado por jogadores de um nível específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ranking por nível retornado com sucesso",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "404", description = "Nível não encontrado")
    })
    public ResponseEntity<Page<RankingResponse>> getRankingByLevel(
            @Parameter(description = "ID do nível", required = true) @PathVariable Long levelId,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(getRankingByLevelService.execute(levelId, pageable));
    }

    @PostMapping("/refresh")
    @IsAdmin
    @Operation(
            summary = "Atualizar cache do ranking",
            description = "Força a atualização do cache de ranking. Apenas administradores podem executar esta ação."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cache atualizado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Apenas administradores podem atualizar o cache")
    })
    public ResponseEntity<String> refreshRanking() {
        refreshRankingCacheService.execute();
        return ResponseEntity.ok("Ranking cache refreshed successfully");
    }
}
