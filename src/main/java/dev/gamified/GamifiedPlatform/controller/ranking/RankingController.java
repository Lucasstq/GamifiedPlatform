package dev.gamified.GamifiedPlatform.controller.ranking;

import dev.gamified.GamifiedPlatform.config.annotations.CanReadUsers;
import dev.gamified.GamifiedPlatform.config.annotations.IsAdmin;
import dev.gamified.GamifiedPlatform.dtos.response.MyRankingResponse;
import dev.gamified.GamifiedPlatform.dtos.response.RankingResponse;
import dev.gamified.GamifiedPlatform.services.ranking.GetGlobalRankingService;
import dev.gamified.GamifiedPlatform.services.ranking.GetMyRankingService;
import dev.gamified.GamifiedPlatform.services.ranking.GetRankingByLevelService;
import dev.gamified.GamifiedPlatform.services.ranking.RefreshRankingCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gerenciar o ranking global de jogadores.
 */
@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final GetGlobalRankingService getGlobalRankingService;
    private final GetMyRankingService getMyRankingService;
    private final GetRankingByLevelService getRankingByLevelService;
    private final RefreshRankingCacheService refreshRankingCacheService;

    /*
     * Busca o ranking global paginado (top 100 por padrão).
     * Público para todos os usuários autenticados.
     */
    @GetMapping
    @CanReadUsers
    public ResponseEntity<List<RankingResponse>> getGlobalRanking(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        // Limita o tamanho máximo da página
        if (size > 100) {
            size = 100;
        }

        return ResponseEntity.ok(getGlobalRankingService.execute(page, size));
    }

    /*
     * Busca a posição do usuário autenticado no ranking.
     */
    @GetMapping("/me")
    @CanReadUsers
    public ResponseEntity<MyRankingResponse> getMyRanking() {
        return ResponseEntity.ok(getMyRankingService.execute());
    }

    /*
     * Busca o ranking filtrado por nível específico.
     */
    @GetMapping("/level/{levelId}")
    @CanReadUsers
    public ResponseEntity<List<RankingResponse>> getRankingByLevel(
            @PathVariable Long levelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        // Limita o tamanho máximo da página
        if (size > 100) {
            size = 100;
        }

        return ResponseEntity.ok(getRankingByLevelService.execute(levelId, page, size));
    }

    /*
     * Força a atualização do cache de ranking (ADMIN).
     */
    @PostMapping("/refresh")
    @IsAdmin
    public ResponseEntity<String> refreshRanking() {
        refreshRankingCacheService.execute();
        return ResponseEntity.ok("Ranking cache refreshed successfully");
    }
}
