package dev.gamified.GamifiedPlatform.repository;

import dev.gamified.GamifiedPlatform.domain.Scopes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScopeRepository extends JpaRepository<Scopes, Long> {

    /*
     * Query otimizada para retornar apenas o scope específico
     */
    @Query("SELECT s FROM Scopes s WHERE s.name = :name")
    Optional<Scopes> findByName(@Param("name") String name);

    /*
     * Usado para atribuir scopes padrão aos usuários baseado em sua role
     */
    @Query("SELECT s FROM Scopes s WHERE s.name IN :names")
    List<Scopes> findByNameIn(@Param("names") List<String> names);

    /**
     * Verifica se existe scope com o nome especificado
     * Query otimizada que retorna apenas boolean sem carregar a entidade
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Scopes s WHERE s.name = :name")
    boolean existsByName(@Param("name") String name);
}

