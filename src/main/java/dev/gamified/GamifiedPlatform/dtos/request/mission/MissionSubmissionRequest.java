package dev.gamified.GamifiedPlatform.dtos.request.mission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionSubmissionRequest {
    @NotBlank(message = "URL do GitHub é obrigatória")
    @Pattern(regexp = "^https://github\\.com/.*", message = "URL deve ser do GitHub")
    private String submissionUrl;
    private String submissionNotes;
}
