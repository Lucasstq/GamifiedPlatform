package dev.gamified.GamifiedPlatform.controller.boss;

import dev.gamified.GamifiedPlatform.config.annotations.CanEvaluateBosses;
import dev.gamified.GamifiedPlatform.config.annotations.CanFightBosses;
import dev.gamified.GamifiedPlatform.config.annotations.CanReadBosses;
import dev.gamified.GamifiedPlatform.config.annotations.IsAdmin;
import dev.gamified.GamifiedPlatform.dtos.request.boss.BossCreateRequest;
import dev.gamified.GamifiedPlatform.dtos.request.boss.BossFightEvaluationRequest;
import dev.gamified.GamifiedPlatform.dtos.request.boss.BossFightSubmissionRequest;
import dev.gamified.GamifiedPlatform.dtos.response.BossProgressResponse;
import dev.gamified.GamifiedPlatform.dtos.response.BossResponse;
import dev.gamified.GamifiedPlatform.dtos.response.UserBossResponse;
import dev.gamified.GamifiedPlatform.services.boss.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bosses")
@RequiredArgsConstructor
public class BossController {

    private final GetAllBossesService getAllBossesService;
    private final CreateBossService createBossService;
    private final CheckBossUnlockService checkBossUnlockService;
    private final StartBossFightService startBossFightService;
    private final SubmitBossFightService submitBossFightService;
    private final EvaluateBossFightService evaluateBossFightService;
    private final GetBossProgressService getBossProgressService;
    private final GetPendingBossEvaluationsService getPendingBossEvaluationsService;
    private final GetMyBossEvaluationsService getMyBossEvaluationsService;

    /*
     * Lista todos os bosses disponíveis no sistema com paginação.
     */
    @GetMapping
    @CanReadBosses
    public ResponseEntity<Page<BossResponse>> getAllBosses(Pageable pageable) {
        return ResponseEntity.ok(getAllBossesService.execute(pageable));
    }

    /*
     * Cria um novo boss para um nível específico.
     * Apenas admins podem criar bosses.
     * Cada nível pode ter apenas um boss.
     */
    @PostMapping
    @IsAdmin
    public ResponseEntity<BossResponse> createBoss(@Valid @RequestBody BossCreateRequest request) {
        return ResponseEntity.ok(createBossService.execute(request));
    }

    /*
     * Verifica o progresso do usuário em um nível e o status do boss.
     * Automaticamente desbloqueia o boss se o usuário atingir 80% de progresso.
     */
    @GetMapping("/level/{levelId}/progress")
    @CanReadBosses
    public ResponseEntity<BossProgressResponse> checkBossUnlock(@PathVariable Long levelId) {
        return ResponseEntity.ok(checkBossUnlockService.execute(levelId));
    }

    /*
     * Obtém o progresso do usuário em relação a um boss específico.
     */
    @GetMapping("/{bossId}/progress")
    @CanReadBosses
    public ResponseEntity<BossProgressResponse> getBossProgress(@PathVariable Long bossId) {
        return ResponseEntity.ok(getBossProgressService.execute(bossId));
    }

    /*
     * Inicia uma luta contra um boss.
     * O boss deve estar desbloqueado (80% de progresso no nível).
     */
    @PostMapping("/{bossId}/start")
    @CanFightBosses
    public ResponseEntity<UserBossResponse> startBossFight(@PathVariable Long bossId) {
        return ResponseEntity.ok(startBossFightService.execute(bossId));
    }

    /*
     * Submete a solução de uma luta contra um boss.
     */
    @PostMapping("/{bossId}/submit")
    @CanFightBosses
    public ResponseEntity<UserBossResponse> submitBossFight(@PathVariable Long bossId,
                                                            @Valid @RequestBody BossFightSubmissionRequest request) {
        return ResponseEntity.ok(submitBossFightService.execute(bossId, request));
    }

    /*
     * Avalia uma submissão de luta contra um boss.
     * Apenas mentores e admins podem avaliar.
     * Se aprovado, concede XP e badge ao usuário.
     */
    @PostMapping("/submissions/{userBossId}/evaluate")
    @CanEvaluateBosses
    public ResponseEntity<UserBossResponse> evaluateBossFight(
            @PathVariable Long userBossId,
            @Valid @RequestBody BossFightEvaluationRequest request) {
        return ResponseEntity.ok(evaluateBossFightService.execute(userBossId, request));
    }

    /*
     * Lista boss fights pendentes de avaliação.
     * Apenas mentores e admins podem acessar.
     */
    @GetMapping("/pending")
    @CanEvaluateBosses
    public ResponseEntity<Page<UserBossResponse>> getPendingEvaluations(Pageable pageable) {
        return ResponseEntity.ok(getPendingBossEvaluationsService.execute(pageable));
    }

    /*
     * Lista avaliações de boss fights feitas pelo mentor autenticado.
     * Apenas mentores e admins podem acessar.
     */
    @GetMapping("/my-evaluations")
    @CanEvaluateBosses
    public ResponseEntity<Page<UserBossResponse>> getMyEvaluations(Pageable pageable) {
        return ResponseEntity.ok(getMyBossEvaluationsService.execute(pageable));
    }
}

