package dev.gamified.GamifiedPlatform.services.boss;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.Boss;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.domain.UserBoss;
import dev.gamified.GamifiedPlatform.dtos.response.UserBossResponse;
import dev.gamified.GamifiedPlatform.enums.BossFightStatus;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.BossMapper;
import dev.gamified.GamifiedPlatform.repository.BossRepository;
import dev.gamified.GamifiedPlatform.repository.UserBossRepository;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Serviço responsável por iniciar uma luta contra um boss.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StartBossFightService {

    private final UserBossRepository userBossRepository;
    private final BossRepository bossRepository;
    private final UserRepository userRepository;

    /*
     * Inicia uma luta contra um boss.
     */
    @Transactional
    public UserBossResponse execute(Long bossId) {

        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("User must be authenticated to start a boss fight"));

        log.info("User {} starting boss fight against boss {}", userId, bossId);

        validateUserPermission(userId);

        User user = findUser(userId);
        Boss boss = findBoss(bossId);

        UserBoss userBoss = findOrCreateUserBoss(user, boss);
        validateBossStatus(userBoss);

        startBossFight(userBoss);

        UserBoss savedUserBoss = userBossRepository.save(userBoss);
        log.info("Boss fight {} successfully started by user {}", bossId, userId);

        return BossMapper.toUserBossResponse(savedUserBoss);
    }

    private void validateUserPermission(Long userId) {
        if (!SecurityUtils.isResourceOwnerOrAdmin(userId)) {
            throw new AccessDeniedException("You do not have permission to access this feature");
        }
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found by ID: " + userId));
    }

    private Boss findBoss(Long bossId) {
        return bossRepository.findById(bossId)
                .orElseThrow(() -> new ResourceNotFoundException("Boss not found by ID: " + bossId));
    }

    private UserBoss findOrCreateUserBoss(User user, Boss boss) {
        return userBossRepository.findByUserIdAndBossId(user.getId(), boss.getId())
                .orElseGet(() -> createLockedUserBoss(user, boss));
    }

    private UserBoss createLockedUserBoss(User user, Boss boss) {
        UserBoss userBoss = UserBoss.builder()
                .user(user)
                .boss(boss)
                .status(BossFightStatus.LOCKED)
                .build();
        return userBossRepository.save(userBoss);
    }

    private void validateBossStatus(UserBoss userBoss) {
        if (userBoss.getStatus() == BossFightStatus.LOCKED) {
            throw new BusinessException("Boss is still locked. Complete 80% of level missions to unlock.");
        }

        if (userBoss.getStatus() == BossFightStatus.DEFEATED) {
            throw new BusinessException("Boss has already been defeated.");
        }

        if (userBoss.getStatus() == BossFightStatus.AWAITING_EVALUATION) {
            throw new BusinessException("Boss fight submission is already awaiting evaluation.");
        }

        if (userBoss.getStatus() == BossFightStatus.IN_PROGRESS) {
            throw new BusinessException("Boss fight is already in progress.");
        }
    }

    private void startBossFight(UserBoss userBoss) {
        userBoss.setStatus(BossFightStatus.IN_PROGRESS);
        userBoss.setStartedAt(LocalDateTime.now());
        // Limpar submissão anterior se estava FAILED
        userBoss.setSubmissionUrl(null);
        userBoss.setSubmissionNotes(null);
        userBoss.setFeedback(null);
    }
}

