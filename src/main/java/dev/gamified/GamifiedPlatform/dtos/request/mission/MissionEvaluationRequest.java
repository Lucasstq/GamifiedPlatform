package dev.gamified.GamifiedPlatform.dtos.request.mission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionEvaluationRequest {
    @NotNull(message = "Status de aprovação é obrigatório")
    private Boolean approved;
    @NotBlank(message = "Feedback é obrigatório")
    private String feedback;
}
