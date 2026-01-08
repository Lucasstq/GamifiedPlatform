package dev.gamified.GamifiedPlatform.services.admin;

import dev.gamified.GamifiedPlatform.domain.Mission;
import dev.gamified.GamifiedPlatform.domain.Boss;
import dev.gamified.GamifiedPlatform.dtos.response.admin.AdminDashboardResponse;
import dev.gamified.GamifiedPlatform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAdminDashboardService {

    private final UserRepository userRepository;
    private final MissionRepository missionRepository;
    private final BossRepository bossRepository;
    private final LevelRepository levelRepository;
    private final BadgeRepository badgeRepository;
    private final UserMissionRepository userMissionRepository;
    private final UserBossRepository userBossRepository;

    @Transactional(readOnly = true)
    public AdminDashboardResponse execute() {
        log.info("Fetching admin dashboard data");

        Long totalUsers = userRepository.count();
        Long activeUsers = userRepository.countActiveUsers();
        Long totalMissions = missionRepository.count();
        Long totalBosses = bossRepository.count();
        Long totalLevels = levelRepository.count();
        Long totalBadges = badgeRepository.count();

        Double averageCompletionRate = calculateAverageCompletionRate();
        AdminDashboardResponse.MissionDifficultyStats hardestMission = getHardestMission();
        AdminDashboardResponse.BossDefeatedStats undefeatedBoss = getMostUndefeatedBoss();

        return AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalMissions(totalMissions)
                .totalBosses(totalBosses)
                .totalLevels(totalLevels)
                .totalBadges(totalBadges)
                .averageCompletionRate(averageCompletionRate)
                .missionDifficultyStats(hardestMission)
                .bossDefeatedStats(undefeatedBoss)
                .build();
    }

    private Double calculateAverageCompletionRate() {
        List<Mission> allMissions = missionRepository.findAll();
        if (allMissions.isEmpty()) {
            return 0.0;
        }

        double totalCompletionRate = 0.0;
        int missionCount = 0;

        for (Mission mission : allMissions) {
            Long totalAttempts = userMissionRepository.countByMissionId(mission.getId());
            if (totalAttempts > 0) {
                Long completed = userMissionRepository.countCompletedByMissionId(mission.getId());
                double completionRate = (completed.doubleValue() / totalAttempts) * 100;
                totalCompletionRate += completionRate;
                missionCount++;
            }
        }

        return missionCount > 0 ? totalCompletionRate / missionCount : 0.0;
    }

    private AdminDashboardResponse.MissionDifficultyStats getHardestMission() {
        List<Mission> allMissions = missionRepository.findAll();

        Mission hardest = null;
        double highestFailureRate = 0.0;
        long maxFailedAttempts = 0L;
        long maxTotalAttempts = 0L;

        for (Mission mission : allMissions) {
            Long totalAttempts = userMissionRepository.countByMissionId(mission.getId());
            if (totalAttempts > 0) {
                Long failed = userMissionRepository.countFailedByMissionId(mission.getId());
                double failureRate = (failed.doubleValue() / totalAttempts) * 100;

                if (failureRate > highestFailureRate) {
                    highestFailureRate = failureRate;
                    hardest = mission;
                    maxFailedAttempts = failed;
                    maxTotalAttempts = totalAttempts;
                }
            }
        }

        if (hardest == null) {
            return null;
        }

        return AdminDashboardResponse.MissionDifficultyStats.builder()
                .missionId(hardest.getId())
                .missionTitle(hardest.getTitle())
                .totalAttempts(maxTotalAttempts)
                .failedAttempts(maxFailedAttempts)
                .failureRate(highestFailureRate)
                .build();
    }

    private AdminDashboardResponse.BossDefeatedStats getMostUndefeatedBoss() {
        List<Boss> allBosses = bossRepository.findAll();

        Boss mostUndefeated = null;
        double lowestDefeatRate = 100.0;
        long minDefeated = Long.MAX_VALUE;
        long maxTotalAttempts = 0L;

        for (Boss boss : allBosses) {
            Long totalAttempts = userBossRepository.countByBossId(boss.getId());
            if (totalAttempts > 0) {
                Long defeated = userBossRepository.countDefeatedByBossId(boss.getId());
                double defeatRate = (defeated.doubleValue() / totalAttempts) * 100;

                if (defeatRate < lowestDefeatRate || (defeatRate == lowestDefeatRate && defeated < minDefeated)) {
                    lowestDefeatRate = defeatRate;
                    mostUndefeated = boss;
                    minDefeated = defeated;
                    maxTotalAttempts = totalAttempts;
                }
            }
        }

        if (mostUndefeated == null) {
            return null;
        }

        return AdminDashboardResponse.BossDefeatedStats.builder()
                .bossId(mostUndefeated.getId())
                .bossName(mostUndefeated.getName())
                .totalAttempts(maxTotalAttempts)
                .defeated(minDefeated)
                .defeatRate(lowestDefeatRate)
                .build();
    }
}
