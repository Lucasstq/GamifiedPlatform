package dev.gamified.GamifiedPlatform.services.boss;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.Boss;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.domain.UserBoss;
import dev.gamified.GamifiedPlatform.dtos.response.bosses.BossProgressResponse;
import dev.gamified.GamifiedPlatform.dtos.response.bosses.UserBossResponse;
import dev.gamified.GamifiedPlatform.enums.BossFightStatus;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.BossMapper;
import dev.gamified.GamifiedPlatform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * Serviço responsável por obter o progresso e status de um boss para um usuário.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GetBossProgressService {

    private final UserBossRepository userBossRepository;
    private final BossRepository bossRepository;
    private final UserRepository userRepository;
    private final MissionRepository missionRepository;
    private final UserMissionRepository userMissionRepository;
    private final LevelRepository levelRepository;

    /*
     * Obtém o progresso do usuário em relação a um boss específico.
     */
    @Transactional(readOnly = true)
    public BossProgressResponse execute(Long bossId) {

        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("User must be authenticated to access boss progress."));

        log.info("Getting boss progress for user {} and boss {}", userId, bossId);

        findUser(userId);
        Boss boss = findBoss(bossId);
        Long levelId = boss.getLevel().getId();

        Long totalMissions = missionRepository.countByLevelId(levelId);
        Long completedMissions = userMissionRepository.countCompletedMissionsByUserAndLevel(userId, levelId);

        double progressPercentage = totalMissions > 0 ? (completedMissions * 100.0) / totalMissions : 0.0;

        UserBoss userBoss = userBossRepository.findByUserIdAndBossId(userId, bossId).orElse(null);
        UserBossResponse bossStatus = userBoss != null ? BossMapper.toUserBossResponse(userBoss) : null;

        return BossProgressResponse.builder()
                .levelId(levelId)
                .levelName(boss.getLevel().getName())
                .totalMissions(totalMissions)
                .completedMissions(completedMissions)
                .progressPercentage(progressPercentage)
                .bossUnlocked(userBoss != null && userBoss.getStatus() != BossFightStatus.LOCKED)
                .bossStatus(bossStatus)
                .build();
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found by ID: " + userId));
    }

    private Boss findBoss(Long bossId) {
        return bossRepository.findById(bossId)
                .orElseThrow(() -> new ResourceNotFoundException("Boss not found by ID: " + bossId));
    }
}

