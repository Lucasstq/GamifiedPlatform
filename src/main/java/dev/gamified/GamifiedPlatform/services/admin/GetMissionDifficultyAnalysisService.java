package dev.gamified.GamifiedPlatform.services.admin;

import dev.gamified.GamifiedPlatform.domain.Mission;
import dev.gamified.GamifiedPlatform.dtos.response.missions.MissionDifficultyAnalysisResponse;
import dev.gamified.GamifiedPlatform.repository.MissionRepository;
import dev.gamified.GamifiedPlatform.repository.UserMissionRepository;
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
public class GetMissionDifficultyAnalysisService {

    private final MissionRepository missionRepository;
    private final UserMissionRepository userMissionRepository;

    @Transactional(readOnly = true)
    public MissionDifficultyAnalysisResponse execute() {
        log.info("Analyzing mission difficulty");

        List<Mission> allMissions = missionRepository.findAll();
        List<MissionDifficultyAnalysisResponse.DifficultMission> missionStats = new ArrayList<>();

        double totalFailureRate = 0.0;
        int missionCount = 0;

        for (Mission mission : allMissions) {
            Long totalSubmissions = userMissionRepository.countByMissionId(mission.getId());

            if (totalSubmissions > 0) {
                Long approved = userMissionRepository.countCompletedByMissionId(mission.getId());
                Long failed = userMissionRepository.countFailedByMissionId(mission.getId());

                double failureRate = (failed.doubleValue() / totalSubmissions) * 100;
                double approvalRate = (approved.doubleValue() / totalSubmissions) * 100;

                totalFailureRate += failureRate;
                missionCount++;

                missionStats.add(MissionDifficultyAnalysisResponse.DifficultMission.builder()
                        .missionId(mission.getId())
                        .missionTitle(mission.getTitle())
                        .levelName(mission.getLevel().getName())
                        .totalSubmissions(totalSubmissions)
                        .approvedSubmissions(approved)
                        .failedSubmissions(failed)
                        .failureRate(failureRate)
                        .approvalRate(approvalRate)
                        .build());
            }
        }

        Double averageFailureRate = missionCount > 0 ? totalFailureRate / missionCount : 0.0;

        List<MissionDifficultyAnalysisResponse.DifficultMission> hardestMissions = missionStats.stream()
                .sorted(Comparator.comparing(MissionDifficultyAnalysisResponse.DifficultMission::failureRate).reversed())
                .limit(10)
                .collect(Collectors.toList());

        List<MissionDifficultyAnalysisResponse.DifficultMission> easiestMissions = missionStats.stream()
                .sorted(Comparator.comparing(MissionDifficultyAnalysisResponse.DifficultMission::failureRate))
                .limit(10)
                .collect(Collectors.toList());

        return MissionDifficultyAnalysisResponse.builder()
                .hardestMissions(hardestMissions)
                .easiestMissions(easiestMissions)
                .averageFailureRate(averageFailureRate)
                .build();
    }
}

