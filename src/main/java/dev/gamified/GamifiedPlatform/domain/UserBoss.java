package dev.gamified.GamifiedPlatform.domain;

import dev.gamified.GamifiedPlatform.enums.BossFightStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_user_bosses", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_boss", columnNames = {"user_id", "boss_id"})
})
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserBoss {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boss_id", nullable = false)
    private Boss boss;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BossFightStatus status = BossFightStatus.LOCKED;

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

    @Column(name = "unlocked_at")
    private LocalDateTime unlockedAt;
}

