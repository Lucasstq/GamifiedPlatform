package dev.gamified.GamifiedPlatform.dtos.response;
import dev.gamified.GamifiedPlatform.enums.MissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMissionResponse {
    private Long id;
    private Long missionId;
    private String missionTitle;
    private String missionDescription;
    private Integer xpReward;
    private Integer orderNumber;
    private MissionStatus status;
    private String submissionUrl;
    private String submissionNotes;
    private String feedback;
    private String evaluatedByName;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private LocalDateTime evaluatedAt;
    private LocalDateTime completedAt;
}
