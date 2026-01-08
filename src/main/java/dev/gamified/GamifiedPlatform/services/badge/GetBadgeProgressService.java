package dev.gamified.GamifiedPlatform.services.badge;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.dtos.response.badges.BadgeProgressResponse;
import dev.gamified.GamifiedPlatform.enums.Roles;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.BadgeRepository;
import dev.gamified.GamifiedPlatform.repository.UserBadgeRepository;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * Serviço para calcular o progresso de badges de um usuário.
 * Mostra quantos badges foram conquistados e quantos faltam.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GetBadgeProgressService {

    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final UserRepository userRepository;

    /*
     * Calcula o progresso de badges de um usuário.
     * Requer permissão: próprio usuário, admin ou mentor.
     */
    @Transactional(readOnly = true)
    public BadgeProgressResponse execute(Long userId) {
        log.info("Calculating badge progress for user {}", userId);

        validateUserPermission(userId);
        validateUserExists(userId);

        Long totalBadges = badgeRepository.count();
        Long unlockedBadges = userBadgeRepository.countByUserId(userId);
        Long remainingBadges = totalBadges - unlockedBadges;

        double progressPercentage = calculateProgressPercentage(totalBadges, unlockedBadges);

        return BadgeProgressResponse.builder()
                .totalBadges(totalBadges)
                .unlockedBadges(unlockedBadges)
                .remainingBadges(remainingBadges)
                .progressPercentage(Math.round(progressPercentage * 100.0) / 100.0)
                .build();
    }

    /*
     * Calcula a porcentagem de progresso arredondada para 2 casas decimais.
     */
    private double calculateProgressPercentage(Long total, Long unlocked) {
        if (total == 0) {
            return 0.0;
        }
        return (unlocked.doubleValue() / total.doubleValue()) * 100.0;
    }

    /*
     * Verifica se o usuário autenticado tem permissão.
     * Permite acesso se for o próprio usuário, admin ou mentor.
     */
    private void validateUserPermission(Long userId) {
        if (!SecurityUtils.isResourceOwnerOrAdmin(userId) && !SecurityUtils.hasRole(Roles.ROLE_MENTOR)) {
            throw new AccessDeniedException("You do not have permission to view this user's badge progress");
        }
    }

    /*
     * Valida se o usuário existe no sistema.
     */
    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
    }
}

