package dev.gamified.GamifiedPlatform.services.boss;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.UserBoss;
import dev.gamified.GamifiedPlatform.dtos.request.boss.BossFightSubmissionRequest;
import dev.gamified.GamifiedPlatform.dtos.response.bosses.UserBossResponse;
import dev.gamified.GamifiedPlatform.enums.BossFightStatus;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.BossMapper;
import dev.gamified.GamifiedPlatform.repository.UserBossRepository;
import dev.gamified.GamifiedPlatform.services.security.RateLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/*
 * Serviço responsável por submeter uma solução para uma luta contra um boss.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubmitBossFightService {

    private final UserBossRepository userBossRepository;
    private final RateLimitService rateLimitService;

    /*
     * Submete a solução de uma luta contra um boss.
     */
    @Transactional
    public UserBossResponse execute(Long bossId, BossFightSubmissionRequest request) {

        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("User must be authenticated to submit a boss fight solution"));

        if (!rateLimitService.isBossSubmissionAllowed(userId)) {
            throw new BusinessException("Rate limit exceeded. You can only submit 3 boss attempts every hour.");
        }

        log.info("User {} submitting boss fight solution for boss {}", userId, bossId);

        validateUserPermission(userId);

        UserBoss userBoss = findUserBoss(userId, bossId);
        validateBossStatus(userBoss);

        submitBossFight(userBoss, request);

        UserBoss savedUserBoss = userBossRepository.save(userBoss);
        log.info("Boss fight solution submitted successfully by user {} for boss {}", userId, bossId);

        return BossMapper.toUserBossResponse(savedUserBoss);
    }

    private void validateUserPermission(Long userId) {
        if (!SecurityUtils.isResourceOwnerOrAdmin(userId)) {
            throw new AccessDeniedException("You do not have permission to access this feature");
        }
    }

    private UserBoss findUserBoss(Long userId, Long bossId) {
        return userBossRepository.findByUserIdAndBossId(userId, bossId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Boss fight not found for user " + userId + " and boss " + bossId));
    }

    private void validateBossStatus(UserBoss userBoss) {
        if (userBoss.getStatus() != BossFightStatus.IN_PROGRESS &&
                userBoss.getStatus() != BossFightStatus.FAILED) {
            throw new BusinessException(
                    "Can only submit solutions for boss fights that are IN_PROGRESS or FAILED. Current status: "
                            + userBoss.getStatus());
        }
    }

    private void submitBossFight(UserBoss userBoss, BossFightSubmissionRequest request) {
        userBoss.setStatus(BossFightStatus.AWAITING_EVALUATION);
        BossMapper.applySubmissionRequest(userBoss, request);
        userBoss.setSubmittedAt(LocalDateTime.now());
    }
}

