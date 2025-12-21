package dev.gamified.GamifiedPlatform.repository;

import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerCharacterRepository extends JpaRepository<PlayerCharacter, Long> {
}
