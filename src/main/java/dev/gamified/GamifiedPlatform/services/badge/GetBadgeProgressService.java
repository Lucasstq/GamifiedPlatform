package dev.gamified.GamifiedPlatform.services.badge;

import dev.gamified.GamifiedPlatform.config.security.PermissionValidator;
import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.constants.BusinessConstants;
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

        PermissionValidator.validateResourceOwnerOrAdmin(userId);
        validateUserExists(userId);

        Long totalBadges = badgeRepository.count();
        Long unlockedBadges = userBadgeRepository.countByUserId(userId);
        Long remainingBadges = totalBadges - unlockedBadges;

        double progressPercentage = calculateProgressPercentage(totalBadges, unlockedBadges);

        return BadgeProgressResponse.builder()
                .totalBadges(totalBadges)
                .unlockedBadges(unlockedBadges)
                .remainingBadges(remainingBadges)
                .progressPercentage(Math.round(progressPercentage * BusinessConstants.MAX_PROGRESS_PERCENTAGE)
                        / BusinessConstants.MAX_PROGRESS_PERCENTAGE)
                .build();
    }

    /*
     * Calcula a porcentagem de progresso arredondada para 2 casas decimais.
     */
    private double calculateProgressPercentage(Long total, Long unlocked) {
        if (total == 0) {
            return BusinessConstants.MIN_PROGRESS_PERCENTAGE;
        }
        return (unlocked.doubleValue() / total.doubleValue()) * BusinessConstants.MAX_PROGRESS_PERCENTAGE;
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

