package dev.gamified.GamifiedPlatform.repository;

import dev.gamified.GamifiedPlatform.domain.Scopes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Scopes, Long> {
}
