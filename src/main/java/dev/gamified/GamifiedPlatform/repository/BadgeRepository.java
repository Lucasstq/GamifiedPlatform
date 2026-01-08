package dev.gamified.GamifiedPlatform.repository;

import dev.gamified.GamifiedPlatform.domain.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {

    @Query("SELECT b FROM Badge b WHERE b.level.id = :levelId")
    Optional<Badge> findByLevelId(@Param("levelId") Long levelId);

    @Query("SELECT b FROM Badge b WHERE b.name = :name")
    Optional<Badge> findByName(@Param("name") String name);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Badge b WHERE b.level.id = :levelId")
    Boolean existsByLevelId(@Param("levelId") Long levelId);
}
