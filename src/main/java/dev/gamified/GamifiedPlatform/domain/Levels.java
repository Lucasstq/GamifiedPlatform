package dev.gamified.GamifiedPlatform.domain;

import dev.gamified.GamifiedPlatform.enums.DifficutyLevel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_levels")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Levels {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_level", unique = true, nullable = false)
    private Integer orderLevel;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(name = "xp_required", nullable = false)
    private Integer xpRequired;

    @Column(name = "icon_url")
    private String iconUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", nullable = false)
    private DifficutyLevel difficultyLevel;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
