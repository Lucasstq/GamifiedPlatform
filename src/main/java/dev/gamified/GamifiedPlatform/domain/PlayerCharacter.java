package dev.gamified.GamifiedPlatform.domain;

import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_player_character")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private Integer level = 1;

    @Column(nullable = false)
    @Builder.Default
    private Integer xp = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /*
     * Adiciona XP ao personagem.
     */
    public void addXp(Integer xpToAdd) {
        if (xpToAdd == null || xpToAdd < 0) {
            throw new BusinessException("XP to add must be a positive number");
        }
        this.xp += xpToAdd;
    }
}
