package dev.gamified.GamifiedPlatform.domain;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_bosses")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Boss {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", nullable = false, unique = true)
    private Levels level;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String challenge;
    @Column(name = "xp_reward", nullable = false)
    private Integer xpReward;
    @Column(name = "badge_name", nullable = false)
    private String badgeName;
    @Column(name = "badge_description", nullable = false)
    private String badgeDescription;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "badge_icon_url")
    private String badgeIconUrl;
    @Column(name = "unlocks_next_level", nullable = false)
    @Builder.Default
    private Boolean unlocksNextLevel = true;
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
