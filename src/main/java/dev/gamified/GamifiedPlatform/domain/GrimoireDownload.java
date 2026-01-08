package dev.gamified.GamifiedPlatform.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/*
 * Entidade que registra os downloads de grimórios pelos usuários.
 * Mantém histórico de acesso aos materiais.
 */
@Entity
@Table(name = "tb_grimoire_downloads")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GrimoireDownload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grimoire_id", nullable = false)
    private Grimoire grimoire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "downloaded_at")
    @Builder.Default
    private LocalDateTime downloadedAt = LocalDateTime.now();

    @Column(name = "user_level_at_download", nullable = false)
    private Integer userLevelAtDownload;
}

