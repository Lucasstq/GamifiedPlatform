package dev.gamified.GamifiedPlatform.services.boss;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.domain.UserBoss;
import dev.gamified.GamifiedPlatform.dtos.request.boss.BossFightEvaluationRequest;
import dev.gamified.GamifiedPlatform.dtos.response.bosses.UserBossResponse;
import dev.gamified.GamifiedPlatform.enums.BossFightStatus;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.BossMapper;
import dev.gamified.GamifiedPlatform.repository.PlayerCharacterRepository;
import dev.gamified.GamifiedPlatform.repository.UserBossRepository;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import dev.gamified.GamifiedPlatform.services.badge.UnlockBadgeService;
import dev.gamified.GamifiedPlatform.services.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/*
 * Serviço responsável por avaliar uma submissão de luta contra um boss.
 * Apenas mentores e admins podem avaliar.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EvaluateBossFightService {

    private final UserBossRepository userBossRepository;
    private final PlayerCharacterRepository playerCharacterRepository;
    private final UserRepository userRepository;
    private final UnlockBadgeService unlockBadgeService;
    private final NotificationService notificationService;

    /*
     * Avalia uma submissão de luta contra um boss.
     * Se aprovado, concede XP e marca o boss como derrotado.
     */
    @Transactional
    public UserBossResponse execute(Long userBossId, BossFightEvaluationRequest request) {

        User evaluatorId = SecurityUtils.getCurrentUserId()
                .flatMap(userRepository::findById)
                .orElseThrow(() -> new ResourceNotFoundException("User user not found"));

        log.info("Evaluator {} evaluating boss fight {}", evaluatorId, userBossId);

        UserBoss userBoss = findUserBoss(userBossId);
        validateBossStatus(userBoss);

        User evaluator = findEvaluator(evaluatorId.getId());

        BossMapper.applyEvaluationRequest(userBoss, request);

        if (request.approved()) {
            approveBossFight(userBoss, evaluator);
            grantRewards(userBoss);
        } else {
            rejectBossFight(userBoss, evaluator);
        }

        UserBoss savedUserBoss = userBossRepository.save(userBoss);
        log.info("Boss fight {} evaluated by {}: {}", userBossId, evaluatorId,
                request.approved() ? "APPROVED" : "REJECTED");

        return BossMapper.toUserBossResponse(savedUserBoss);
    }

    private UserBoss findUserBoss(Long userBossId) {
        return userBossRepository.findById(userBossId)
                .orElseThrow(() -> new ResourceNotFoundException("Boss fight not found by ID: " + userBossId));
    }

    private void validateBossStatus(UserBoss userBoss) {
        if (userBoss.getStatus() != BossFightStatus.AWAITING_EVALUATION) {
            throw new BusinessException(
                    "Can only evaluate boss fights with status AWAITING_EVALUATION. Current status: "
                    + userBoss.getStatus());
        }
    }

    private User findEvaluator(Long evaluatorId) {
        return userRepository.findById(evaluatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluator not found by ID: " + evaluatorId));
    }

    private void approveBossFight(UserBoss userBoss, User evaluator) {
        userBoss.setStatus(BossFightStatus.DEFEATED);
        userBoss.setEvaluatedBy(evaluator);
        userBoss.setEvaluatedAt(LocalDateTime.now());
        userBoss.setCompletedAt(LocalDateTime.now());

        notificationService.createBossEvaluatedNotification(
                userBoss.getUser(),
                userBoss.getBoss().getName(),
                true,
                userBoss.getBoss().getId()
        );
    }

    private void rejectBossFight(UserBoss userBoss, User evaluator) {
        userBoss.setStatus(BossFightStatus.FAILED);
        userBoss.setEvaluatedBy(evaluator);
        userBoss.setEvaluatedAt(LocalDateTime.now());

        notificationService.createBossEvaluatedNotification(
                userBoss.getUser(),
                userBoss.getBoss().getName(),
                false,
                userBoss.getBoss().getId()
        );
    }

    private void grantRewards(UserBoss userBoss) {
        PlayerCharacter character = playerCharacterRepository.findByUserId(userBoss.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Player character not found for user: " + userBoss.getUser().getId()));

        Integer xpReward = userBoss.getBoss().getXpReward();
        character.addXp(xpReward);
        playerCharacterRepository.save(character);

        log.info("Granted {} XP to user {} for defeating boss {}",
                xpReward, userBoss.getUser().getId(), userBoss.getBoss().getName());

        // Desbloqueia o badge associado ao boss derrotado
        try {
            unlockBadgeService.execute(
                    userBoss.getUser().getId(),
                    userBoss.getBoss().getLevel().getId(),
                    userBoss.getBoss().getId()
            );
            log.info("Badge '{}' awarded to user {} for defeating boss {}",
                    userBoss.getBoss().getBadgeName(),
                    userBoss.getUser().getId(),
                    userBoss.getBoss().getName());
        } catch (Exception e) {
            log.error("Failed to unlock badge for user {}: {}",
                    userBoss.getUser().getId(), e.getMessage());
            // Não falha a operação de avaliação se o badge falhar
            // O badge pode ser concedido manualmente depois se necessário
        }
    }
}

