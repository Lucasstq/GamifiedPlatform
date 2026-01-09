package dev.gamified.GamifiedPlatform.controller.badge;

import dev.gamified.GamifiedPlatform.config.annotations.CanReadProfile;
import dev.gamified.GamifiedPlatform.dtos.response.badges.BadgeProgressResponse;
import dev.gamified.GamifiedPlatform.dtos.response.badges.BadgeResponse;
import dev.gamified.GamifiedPlatform.dtos.response.user.UserBadgeResponse;
import dev.gamified.GamifiedPlatform.services.badge.GetAllBadgesService;
import dev.gamified.GamifiedPlatform.services.badge.GetBadgeProgressService;
import dev.gamified.GamifiedPlatform.services.badge.GetUserBadgesService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller para gerenciar badges no sistema.
 * Endpoints públicos e privados para visualização de badges.
 */
@RestController
@RequestMapping("/badges")
@RequiredArgsConstructor
@Tag(name = "Badges", description = "Sistema de conquistas e distintivos")
@SecurityRequirement(name = "bearerAuth")
public class BadgeController {

    private final GetAllBadgesService getAllBadgesService;
    private final GetUserBadgesService getUserBadgesService;
    private final GetBadgeProgressService getBadgeProgressService;

    @GetMapping
    @Operation(
            summary = "Listar todos os badges",
            description = "Retorna uma lista paginada de todos os badges disponíveis no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de badges retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<BadgeResponse>> getAllBadges(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(getAllBadgesService.execute(pageable));
    }

    @GetMapping("/user/{userId}")
    @CanReadProfile
    @Operation(
            summary = "Listar badges do usuário",
            description = "Retorna uma lista paginada de todos os badges conquistados por um usuário específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de badges do usuário retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Page<UserBadgeResponse>> getUserBadges(
            @Parameter(description = "ID do usuário", required = true) @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "unlockedAt") Pageable pageable) {
        return ResponseEntity.ok(getUserBadgesService.execute(userId, pageable));
    }

    @GetMapping("/user/{userId}/progress")
    @CanReadProfile
    @Operation(
            summary = "Obter progresso de badges",
            description = "Retorna o progresso de badges de um usuário (quantidade conquistada vs total disponível)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Progresso de badges retornado com sucesso",
                    content = @Content(schema = @Schema(implementation = BadgeProgressResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<BadgeProgressResponse> getBadgeProgress(
            @Parameter(description = "ID do usuário", required = true) @PathVariable Long userId) {
        return ResponseEntity.ok(getBadgeProgressService.execute(userId));
    }
}

