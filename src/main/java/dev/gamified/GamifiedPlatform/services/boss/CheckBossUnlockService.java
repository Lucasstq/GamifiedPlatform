package dev.gamified.GamifiedPlatform.services.boss;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.Boss;
import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.domain.UserBoss;
import dev.gamified.GamifiedPlatform.dtos.response.bosses.BossProgressResponse;
import dev.gamified.GamifiedPlatform.dtos.response.bosses.UserBossResponse;
import dev.gamified.GamifiedPlatform.enums.BossFightStatus;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.BossMapper;
import dev.gamified.GamifiedPlatform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Serviço responsável por verificar e desbloquear bosses quando o usuário
 * alcança 80% de progresso nas missões de um nível.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CheckBossUnlockService {

    private final UserMissionRepository userMissionRepository;
    private final BossRepository bossRepository;
    private final UserBossRepository userBossRepository;
    private final MissionRepository missionRepository;
    private final LevelRepository levelRepository;
    private final UserRepository userRepository;

    /*
     * Verifica o progresso do usuário em um nível e desbloqueia o boss se necessário.
     */
    @Transactional
    public BossProgressResponse execute(Long levelId) {

        User currentUser = SecurityUtils.getCurrentUserId()
                .flatMap(userRepository::findById)
                .orElseThrow(() -> new ResourceNotFoundException("User user not found"));

        log.info("Checking boss unlock for user {} on level {}", currentUser.getId(), levelId);

        Levels level = findLevel(levelId);
        Boss boss = findBossByLevel(levelId);

        Long totalMissions = missionRepository.countByLevelId(levelId);
        Long completedMissions = userMissionRepository.countCompletedMissionsByUserAndLevel(currentUser.getId(), levelId);

        if (totalMissions == 0) {
            throw new BusinessException("No missions found for this level");
        }

        double progressPercentage = (completedMissions * 100.0) / totalMissions;
        boolean shouldUnlock = progressPercentage >= 80.0;

        UserBoss userBoss = findOrCreateUserBoss(currentUser, boss);

        // Se deve desbloquear e ainda está locked, desbloqueia
        if (shouldUnlock && userBoss.getStatus() == BossFightStatus.LOCKED) {
            unlockBoss(userBoss);
            userBoss = userBossRepository.save(userBoss);
            log.info("Boss {} unlocked for user {}", boss.getName(), currentUser.getId());
        }

        UserBossResponse bossStatus = userBoss != null ? BossMapper.toUserBossResponse(userBoss) : null;

        return BossProgressResponse.builder()
                .levelId(levelId)
                .levelName(level.getName())
                .totalMissions(totalMissions)
                .completedMissions(completedMissions)
                .progressPercentage(progressPercentage)
                .bossUnlocked(userBoss.getStatus() != BossFightStatus.LOCKED)
                .bossStatus(bossStatus)
                .build();
    }

    private Levels findLevel(Long levelId) {
        return levelRepository.findById(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found by ID: " + levelId));
    }

    private Boss findBossByLevel(Long levelId) {
        return bossRepository.findByLevelId(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Boss not found for level ID: " + levelId));
    }

    private UserBoss findOrCreateUserBoss(User user, Boss boss) {
        return userBossRepository.findByUserIdAndBossId(user.getId(), boss.getId())
                .orElseGet(() -> createUserBoss(user, boss));
    }

    private UserBoss createUserBoss(User user, Boss boss) {
        UserBoss userBoss = UserBoss.builder()
                .user(user)
                .boss(boss)
                .status(BossFightStatus.LOCKED)
                .build();
        return userBossRepository.save(userBoss);
    }

    private void unlockBoss(UserBoss userBoss) {
        userBoss.setStatus(BossFightStatus.UNLOCKED);
        userBoss.setUnlockedAt(LocalDateTime.now());
    }
}

