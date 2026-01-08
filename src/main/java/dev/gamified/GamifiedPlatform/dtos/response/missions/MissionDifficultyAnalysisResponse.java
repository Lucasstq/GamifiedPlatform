package dev.gamified.GamifiedPlatform.dtos.response.missions;

import lombok.Builder;

import java.util.List;

@Builder
public record MissionDifficultyAnalysisResponse(
        List<DifficultMission> hardestMissions,
        List<DifficultMission> easiestMissions,
        Double averageFailureRate
) {
    @Builder
    public record DifficultMission(
            Long missionId,
            String missionTitle,
            String levelName,
            Long totalSubmissions,
            Long approvedSubmissions,
            Long failedSubmissions,
            Double failureRate,
            Double approvalRate
    ) {}
}
