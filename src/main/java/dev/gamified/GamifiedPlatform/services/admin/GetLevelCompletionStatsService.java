package dev.gamified.GamifiedPlatform.services.admin;

import dev.gamified.GamifiedPlatform.constants.BusinessConstants;
import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.domain.Mission;
import dev.gamified.GamifiedPlatform.dtos.response.levels.LevelCompletionStatsResponse;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import dev.gamified.GamifiedPlatform.repository.MissionRepository;
import dev.gamified.GamifiedPlatform.repository.UserMissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetLevelCompletionStatsService {

    private final LevelRepository levelRepository;
    private final MissionRepository missionRepository;
    private final UserMissionRepository userMissionRepository;

    @Transactional(readOnly = true)
    public List<LevelCompletionStatsResponse> execute() {
        log.info("Fetching level completion statistics");

        List<Levels> allLevels = levelRepository.findAll();
        List<LevelCompletionStatsResponse> stats = new ArrayList<>();

        for (Levels level : allLevels) {
            Long totalMissions = missionRepository.countByLevelId(level.getId());
            Long totalUsers = userMissionRepository.countDistinctUsersByLevelId(level.getId());

            // Calcula usuários que completaram o nível (simplificado)
            Long usersCompleted = 0L;

            Double completionRate = totalUsers > 0 ? (usersCompleted.doubleValue() / totalUsers) *
                    BusinessConstants.PERCENTAGE_MULTIPLIER : BusinessConstants.DEFAULT_RATE;

            List<LevelCompletionStatsResponse.MissionStatsInLevel> missionStats =
                    getMissionStatsForLevel(level.getId());

            stats.add(LevelCompletionStatsResponse.builder()
                    .levelId(level.getId())
                    .levelName(level.getName())
                    .orderLevel(level.getOrderLevel())
                    .totalUsers(totalUsers)
                    .usersCompleted(usersCompleted)
                    .completionRate(completionRate)
                    .totalMissions(totalMissions)
                    .averageProgress(BusinessConstants.DEFAULT_RATE)
                    .missionStats(missionStats)
                    .build());
        }

        return stats;
    }

    private List<LevelCompletionStatsResponse.MissionStatsInLevel> getMissionStatsForLevel(Long levelId) {
        List<Mission> missions = missionRepository.findByLevelIdOrderByOrderNumberAsc(levelId);
        List<LevelCompletionStatsResponse.MissionStatsInLevel> stats = new ArrayList<>();

        for (Mission mission : missions) {
            Long totalAttempts = userMissionRepository.countByMissionId(mission.getId());
            Long completed = userMissionRepository.countCompletedByMissionId(mission.getId());
            Long failed = userMissionRepository.countFailedByMissionId(mission.getId());

            Double completionRate = totalAttempts > 0 ?
                    (completed.doubleValue() / totalAttempts) * BusinessConstants.PERCENTAGE_MULTIPLIER :
                    BusinessConstants.DEFAULT_RATE;

            stats.add(LevelCompletionStatsResponse.MissionStatsInLevel.builder()
                    .missionId(mission.getId())
                    .missionTitle(mission.getTitle())
                    .totalAttempts(totalAttempts)
                    .completed(completed)
                    .failed(failed)
                    .completionRate(completionRate)
                    .build());
        }

        return stats;
    }
}

