package dev.gamified.GamifiedPlatform.mapper;

import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.domain.Mission;
import dev.gamified.GamifiedPlatform.domain.UserMission;
import dev.gamified.GamifiedPlatform.dtos.request.mission.MissionCreateRequest;
import dev.gamified.GamifiedPlatform.dtos.request.mission.MissionUpdateRequest;
import dev.gamified.GamifiedPlatform.dtos.response.MissionResponse;
import dev.gamified.GamifiedPlatform.dtos.response.UserMissionResponse;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class MissionMapper {

    public static MissionResponse toResponse(Mission mission) {
        return MissionResponse.builder()
                .id(mission.getId())
                .levelId(mission.getLevel().getId())
                .levelName(mission.getLevel().getName())
                .title(mission.getTitle())
                .description(mission.getDescription())
                .xpReward(mission.getXpReward())
                .orderNumber(mission.getOrderNumber())
                .createdAt(mission.getCreatedAt())
                .updatedAt(mission.getUpdatedAt())
                .build();
    }

    public static Mission toEntity(MissionCreateRequest request, Levels levels) {
        return Mission.builder()
                .level(levels)
                .title(request.title())
                .description(request.description())
                .xpReward(request.xpReward())
                .orderNumber(request.orderNumber())
                .build();
    }

    public static void updateEntity(Mission mission, MissionUpdateRequest request){
        mission.setTitle(request.title());
        mission.setDescription(request.description());
        mission.setXpReward(request.xpReward());
        mission.setOrderNumber(request.orderNumber());
        mission.setUpdatedAt(LocalDateTime.now());
    }

    public static UserMissionResponse toUserMissionResponse(UserMission userMission) {
        return UserMissionResponse.builder()
                .id(userMission.getId())
                .missionId(userMission.getMission().getId())
                .missionTitle(userMission.getMission().getTitle())
                .missionDescription(userMission.getMission().getDescription())
                .xpReward(userMission.getMission().getXpReward())
                .orderNumber(userMission.getMission().getOrderNumber())
                .status(userMission.getStatus())
                .submissionUrl(userMission.getSubmissionUrl())
                .submissionNotes(userMission.getSubmissionNotes())
                .feedback(userMission.getFeedback())
                .evaluatedByName(userMission.getEvaluatedBy() != null ?
                        userMission.getEvaluatedBy().getUsername() : null)
                .startedAt(userMission.getStartedAt())
                .submittedAt(userMission.getSubmittedAt())
                .evaluatedAt(userMission.getEvaluatedAt())
                .completedAt(userMission.getCompletedAt())
                .build();
    }
}

