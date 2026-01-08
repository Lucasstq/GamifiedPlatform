package dev.gamified.GamifiedPlatform.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/*
 * Entidade que representa um grimório (PDF) associado a um nível.
 * Grimórios são materiais de estudo desbloqueados ao atingir determinado nível.
 */
@Entity
@Table(name = "tb_grimoires")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Grimoire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", nullable = false)
    private Levels level;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "minio_bucket", nullable = false)
    private String minioBucket;

    @Column(name = "minio_object_key", nullable = false, length = 500)
    private String minioObjectKey;

    @Column(length = 1000)
    private String description;

    @Column(name = "uploaded_at")
    @Builder.Default
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;
}
