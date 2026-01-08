package dev.gamified.GamifiedPlatform.repository;

import dev.gamified.GamifiedPlatform.domain.Grimoire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GrimoireRepository extends JpaRepository<Grimoire, Long> {

    // Busca grimório por ID do nível.
    Optional<Grimoire> findByLevelId(Long levelId);

    // Verifica se existe grimório para um nível.
    boolean existsByLevelId(Long levelId);

    // Lista todos os grimórios ordenados por nível.
    @Query("SELECT g FROM Grimoire g JOIN FETCH g.level l ORDER BY l.orderLevel ASC")
    List<Grimoire> findAllOrderedByLevel();

    // Busca grimórios que o usuário pode acessar (nível do usuário >= nível do grimório).
    @Query("SELECT g FROM Grimoire g " +
           "JOIN FETCH g.level l " +
           "WHERE l.orderLevel <= :userLevel " +
           "ORDER BY l.orderLevel ASC")
    List<Grimoire> findAccessibleGrimoires(@Param("userLevel") Integer userLevel);
}
