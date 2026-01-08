package dev.gamified.GamifiedPlatform.controller.badge;

import dev.gamified.GamifiedPlatform.config.annotations.CanReadProfile;
import dev.gamified.GamifiedPlatform.dtos.response.BadgeProgressResponse;
import dev.gamified.GamifiedPlatform.dtos.response.BadgeResponse;
import dev.gamified.GamifiedPlatform.dtos.response.UserBadgeResponse;
import dev.gamified.GamifiedPlatform.services.badge.GetAllBadgesService;
import dev.gamified.GamifiedPlatform.services.badge.GetBadgeProgressService;
import dev.gamified.GamifiedPlatform.services.badge.GetUserBadgesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
 * Controller para gerenciar badges no sistema.
 * Endpoints públicos e privados para visualização de badges.
 */
@RestController
@RequestMapping("/badges")
@RequiredArgsConstructor
public class BadgeController {

    private final GetAllBadgesService getAllBadgesService;
    private final GetUserBadgesService getUserBadgesService;
    private final GetBadgeProgressService getBadgeProgressService;

    /*
     * Lista todos os badges disponíveis no sistema.
     * Endpoint público - qualquer usuário autenticado pode ver.
     */
    @GetMapping
    public ResponseEntity<List<BadgeResponse>> getAllBadges() {
        return ResponseEntity.ok(getAllBadgesService.execute());
    }

    /*
     * Lista todos os badges conquistados por um usuário específico.
     * Requer permissão para visualizar o perfil do usuário.
     */
    @GetMapping("/user/{userId}")
    @CanReadProfile
    public ResponseEntity<List<UserBadgeResponse>> getUserBadges(@PathVariable Long userId) {
        return ResponseEntity.ok(getUserBadgesService.execute(userId));
    }

    /*
     * Obtém o progresso de badges de um usuário (quantos conquistou vs total).
     * Requer permissão para visualizar o perfil do usuário.
     */
    @GetMapping("/user/{userId}/progress")
    @CanReadProfile
    public ResponseEntity<BadgeProgressResponse> getBadgeProgress(@PathVariable Long userId) {
        return ResponseEntity.ok(getBadgeProgressService.execute(userId));
    }
}

