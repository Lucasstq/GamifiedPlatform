package dev.gamified.GamifiedPlatform.repository;

import dev.gamified.GamifiedPlatform.domain.GrimoireDownload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrimoireDownloadRepository extends JpaRepository<GrimoireDownload, Long> {

    // Busca downloads de um grimório específico.
    List<GrimoireDownload> findByGrimoireIdOrderByDownloadedAtDesc(Long grimoireId);

    // Busca downloads de um usuário específico.
    List<GrimoireDownload> findByUserIdOrderByDownloadedAtDesc(Long userId);

    // Conta quantas vezes um grimório foi baixado.
    Long countByGrimoireId(Long grimoireId);

    // Conta quantos grimórios únicos um usuário baixou.
    @Query("SELECT COUNT(DISTINCT gd.grimoire.id) FROM GrimoireDownload gd WHERE gd.user.id = :userId")
    Long countDistinctGrimoiresByUserId(@Param("userId") Long userId);

    // Verifica se o usuário já baixou um grimório específico.
    boolean existsByGrimoireIdAndUserId(Long grimoireId, Long userId);
}

