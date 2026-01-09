package dev.gamified.GamifiedPlatform.controller.boss;

import dev.gamified.GamifiedPlatform.config.annotations.CanEvaluateBosses;
import dev.gamified.GamifiedPlatform.config.annotations.CanFightBosses;
import dev.gamified.GamifiedPlatform.config.annotations.CanReadBosses;
import dev.gamified.GamifiedPlatform.config.annotations.IsAdmin;
import dev.gamified.GamifiedPlatform.dtos.request.boss.BossCreateRequest;
import dev.gamified.GamifiedPlatform.dtos.request.boss.BossFightEvaluationRequest;
import dev.gamified.GamifiedPlatform.dtos.request.boss.BossFightSubmissionRequest;
import dev.gamified.GamifiedPlatform.dtos.response.bosses.BossProgressResponse;
import dev.gamified.GamifiedPlatform.dtos.response.bosses.BossResponse;
import dev.gamified.GamifiedPlatform.dtos.response.bosses.UserBossResponse;
import dev.gamified.GamifiedPlatform.services.boss.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bosses")
@RequiredArgsConstructor
@Tag(name = "Bosses", description = "Desafios épicos ao final de cada nível")
@SecurityRequirement(name = "bearerAuth")
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

    @GetMapping
    @CanReadBosses
    @Operation(
            summary = "Listar todos os bosses",
            description = "Retorna uma lista paginada de todos os bosses disponíveis no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de bosses retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<BossResponse>> getAllBosses(Pageable pageable) {
        return ResponseEntity.ok(getAllBossesService.execute(pageable));
    }

    @PostMapping
    @IsAdmin
    @Operation(
            summary = "Criar novo boss",
            description = "Cria um novo boss para um nível específico. Cada nível pode ter apenas um boss. Apenas administradores."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Boss criado com sucesso",
                    content = @Content(schema = @Schema(implementation = BossResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou nível já possui boss"),
            @ApiResponse(responseCode = "403", description = "Apenas administradores podem criar bosses")
    })
    public ResponseEntity<BossResponse> createBoss(@Valid @RequestBody BossCreateRequest request) {
        return ResponseEntity.ok(createBossService.execute(request));
    }

    @Operation(
            summary = "Verificar progresso do boss no nível",
            description = "Verifica o progresso do usuário em um nível e o status do boss. Desbloqueia automaticamente o boss ao atingir 80% de progresso."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Progresso retornado com sucesso",
                    content = @Content(schema = @Schema(implementation = BossProgressResponse.class))),
            @ApiResponse(responseCode = "404", description = "Nível ou boss não encontrado")
    })
    @GetMapping("/level/{levelId}/progress")
    @CanReadBosses
    public ResponseEntity<BossProgressResponse> checkBossUnlock(@PathVariable Long levelId) {
        return ResponseEntity.ok(checkBossUnlockService.execute(levelId));
    }

    @Operation(
            summary = "Obter progresso do boss",
            description = "Retorna o progresso do usuário em relação a um boss específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Progresso retornado com sucesso",
                    content = @Content(schema = @Schema(implementation = BossProgressResponse.class))),
            @ApiResponse(responseCode = "404", description = "Boss não encontrado")
    })
    @GetMapping("/{bossId}/progress")
    @CanReadBosses
    public ResponseEntity<BossProgressResponse> getBossProgress(@PathVariable Long bossId) {
        return ResponseEntity.ok(getBossProgressService.execute(bossId));
    }

    @Operation(
            summary = "Iniciar luta contra boss",
            description = "Inicia uma luta contra um boss. O boss deve estar desbloqueado (80% de progresso no nível)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Luta iniciada com sucesso",
                    content = @Content(schema = @Schema(implementation = UserBossResponse.class))),
            @ApiResponse(responseCode = "400", description = "Boss ainda não desbloqueado"),
            @ApiResponse(responseCode = "404", description = "Boss não encontrado")
    })
    @PostMapping("/{bossId}/start")
    @CanFightBosses
    public ResponseEntity<UserBossResponse> startBossFight(@PathVariable Long bossId) {
        return ResponseEntity.ok(startBossFightService.execute(bossId));
    }

    @Operation(
            summary = "Submeter solução da luta",
            description = "Submete a solução de uma luta contra um boss para avaliação por um mentor"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solução submetida com sucesso",
                    content = @Content(schema = @Schema(implementation = UserBossResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou luta não iniciada"),
            @ApiResponse(responseCode = "404", description = "Boss não encontrado")
    })
    @PostMapping("/{bossId}/submit")
    @CanFightBosses
    public ResponseEntity<UserBossResponse> submitBossFight(@PathVariable Long bossId,
                                                            @Valid @RequestBody BossFightSubmissionRequest request) {
        return ResponseEntity.ok(submitBossFightService.execute(bossId, request));
    }

    @Operation(
            summary = "Avaliar submissão de luta",
            description = "Avalia uma submissão de luta contra um boss. Se aprovado, concede XP e badge. Apenas mentores e admins."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avaliação realizada com sucesso",
                    content = @Content(schema = @Schema(implementation = UserBossResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Apenas mentores e admins podem avaliar"),
            @ApiResponse(responseCode = "404", description = "Submissão não encontrada")
    })
    @PostMapping("/submissions/{userBossId}/evaluate")
    @CanEvaluateBosses
    public ResponseEntity<UserBossResponse> evaluateBossFight(
            @PathVariable Long userBossId,
            @Valid @RequestBody BossFightEvaluationRequest request) {
        return ResponseEntity.ok(evaluateBossFightService.execute(userBossId, request));
    }

    @Operation(
            summary = "Listar avaliações pendentes",
            description = "Lista boss fights pendentes de avaliação. Apenas mentores e admins podem acessar."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de avaliações pendentes",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Apenas mentores e admins podem acessar")
    })
    @GetMapping("/pending")
    @CanEvaluateBosses
    public ResponseEntity<Page<UserBossResponse>> getPendingEvaluations(Pageable pageable) {
        return ResponseEntity.ok(getPendingBossEvaluationsService.execute(pageable));
    }

    @Operation(
            summary = "Listar minhas avaliações",
            description = "Lista avaliações de boss fights feitas pelo mentor autenticado. Apenas mentores e admins."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de avaliações realizadas",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Apenas mentores e admins podem acessar")
    })
    @GetMapping("/my-evaluations")
    @CanEvaluateBosses
    public ResponseEntity<Page<UserBossResponse>> getMyEvaluations(Pageable pageable) {
        return ResponseEntity.ok(getMyBossEvaluationsService.execute(pageable));
    }
}

