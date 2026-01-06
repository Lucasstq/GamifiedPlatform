package dev.gamified.GamifiedPlatform.dtos.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionProgressResponse {
    private Long levelId;
    private String levelName;
    private Long totalMissions;
    private Long completedMissions;
    private Double progressPercentage;
    private Boolean canUnlockBoss;
}
