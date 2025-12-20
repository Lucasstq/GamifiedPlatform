package dev.gamified.GamifiedPlatform.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_character")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer level = 1;

    @Column(nullable = false)
    private Integer xp = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public void calculateLevel() {
        if (xp >= 15000) this.level = 6;
        else if (xp >= 10000) this.level = 5;
        else if (xp >= 6000) this.level = 4;
        else if (xp >= 3000) this.level = 3;
        else if (xp >= 1000) this.level = 2;
        else this.level = 1;
    }

    public void addXp(Integer xpToAdd) {
        this.xp += xpToAdd;
        calculateLevel();
    }
}
