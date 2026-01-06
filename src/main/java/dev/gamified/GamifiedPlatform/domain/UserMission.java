package dev.gamified.GamifiedPlatform.domain;

import dev.gamified.GamifiedPlatform.enums.MissionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_user_missions", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_mission", columnNames = {"user_id", "mission_id"})
})
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MissionStatus status = MissionStatus.AVAILABLE;

    @Column(name = "submission_url", length = 500)
    private String submissionUrl;

    @Column(name = "submission_notes", columnDefinition = "TEXT")
    private String submissionNotes;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluated_by")
    private User evaluatedBy;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "evaluated_at")
    private LocalDateTime evaluatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}

