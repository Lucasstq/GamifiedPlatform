package dev.gamified.GamifiedPlatform.controller.ranking;

import dev.gamified.GamifiedPlatform.config.annotations.CanReadUsers;
import dev.gamified.GamifiedPlatform.config.annotations.IsAdmin;
import dev.gamified.GamifiedPlatform.dtos.response.ranking.MyRankingResponse;
import dev.gamified.GamifiedPlatform.dtos.response.ranking.RankingResponse;
import dev.gamified.GamifiedPlatform.services.ranking.GetGlobalRankingService;
import dev.gamified.GamifiedPlatform.services.ranking.GetMyRankingService;
import dev.gamified.GamifiedPlatform.services.ranking.GetRankingByLevelService;
import dev.gamified.GamifiedPlatform.services.ranking.RefreshRankingCacheService;
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
    public ResponseEntity<Page<RankingResponse>> getGlobalRanking(
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(getGlobalRankingService.execute(pageable));
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
     * Busca o ranking filtrado por nível específico paginado.
     */
    @GetMapping("/level/{levelId}")
    @CanReadUsers
    public ResponseEntity<Page<RankingResponse>> getRankingByLevel(
            @PathVariable Long levelId,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(getRankingByLevelService.execute(levelId, pageable));
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
