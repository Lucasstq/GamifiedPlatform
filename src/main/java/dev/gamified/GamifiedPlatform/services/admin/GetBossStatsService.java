package dev.gamified.GamifiedPlatform.services.admin;

import dev.gamified.GamifiedPlatform.domain.Boss;
import dev.gamified.GamifiedPlatform.dtos.response.bosses.BossStatsResponse;
import dev.gamified.GamifiedPlatform.repository.BossRepository;
import dev.gamified.GamifiedPlatform.repository.UserBossRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetBossStatsService {

    private final BossRepository bossRepository;
    private final UserBossRepository userBossRepository;

    @Transactional(readOnly = true)
    public BossStatsResponse execute() {
        log.info("Fetching boss statistics");

        List<Boss> allBosses = bossRepository.findAll();
        List<BossStatsResponse.BossStat> bossStats = new ArrayList<>();

        long totalAttempts = 0;
        double totalDefeatRate = 0.0;
        int bossCount = 0;

        for (Boss boss : allBosses) {
            Long attempts = userBossRepository.countByBossId(boss.getId());
            Long defeats = userBossRepository.countDefeatedByBossId(boss.getId());
            Long failures = userBossRepository.countFailedByBossId(boss.getId());

            double defeatRate = attempts > 0 ? (defeats.doubleValue() / attempts) * 100 : 0.0;
            double failureRate = attempts > 0 ? (failures.doubleValue() / attempts) * 100 : 0.0;

            totalAttempts += attempts;
            totalDefeatRate += defeatRate;
            bossCount++;

            bossStats.add(BossStatsResponse.BossStat.builder()
                    .bossId(boss.getId())
                    .bossName(boss.getName())
                    .levelName(boss.getLevel().getName())
                    .totalAttempts(attempts)
                    .totalDefeats(defeats)
                    .totalFailures(failures)
                    .defeatRate(defeatRate)
                    .failureRate(failureRate)
                    .build());
        }

        Double averageDefeatRate = bossCount > 0 ? totalDefeatRate / bossCount : 0.0;

        List<BossStatsResponse.BossStat> undefeatedBosses = bossStats.stream()
                .filter(stat -> stat.totalDefeats() == 0)
                .sorted(Comparator.comparing(BossStatsResponse.BossStat::totalAttempts).reversed())
                .collect(Collectors.toList());

        List<BossStatsResponse.BossStat> mostDefeatedBosses = bossStats.stream()
                .sorted(Comparator.comparing(BossStatsResponse.BossStat::defeatRate).reversed())
                .limit(10)
                .collect(Collectors.toList());

        List<BossStatsResponse.BossStat> hardestBosses = bossStats.stream()
                .sorted(Comparator.comparing(BossStatsResponse.BossStat::failureRate).reversed())
                .limit(10)
                .collect(Collectors.toList());

        return BossStatsResponse.builder()
                .undefeatedBosses(undefeatedBosses)
                .mostDefeatedBosses(mostDefeatedBosses)
                .hardestBosses(hardestBosses)
                .totalBossesCount((long) allBosses.size())
                .totalBossAttempts(totalAttempts)
                .averageDefeatRate(averageDefeatRate)
                .build();
    }
}

