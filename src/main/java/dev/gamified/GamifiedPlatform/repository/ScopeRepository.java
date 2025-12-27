package dev.gamified.GamifiedPlatform.repository;

import dev.gamified.GamifiedPlatform.domain.Scopes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScopeRepository extends JpaRepository<Scopes, Long> {

    Optional<Scopes> findByName(String name);

    List<Scopes> findByNameIn(List<String> names);

    boolean existsByName(String name);
}

