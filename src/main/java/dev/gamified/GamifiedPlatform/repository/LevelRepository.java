package dev.gamified.GamifiedPlatform.repository;

import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.enums.DifficultyLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface LevelRepository extends JpaRepository<Levels, Long> {

    Optional<Levels> findByOrderLevel(Integer orderLevel);

    List<Levels> findByDifficultyLevel(DifficultyLevel difficultyLevel);

    Page<Levels> findAllByOrderByOrderLevelAsc(Pageable pageable);

    List<Levels> findAllByOrderByOrderLevelAsc();

    boolean existsByOrderLevel(Integer orderLevel);

    Optional<Levels> findTopByOrderLevelLessThanEqualOrderByOrderLevelDesc(Integer orderLevel);

    @Query("SELECT l FROM Levels l WHERE l.orderLevel IN :orderLevels")
    List<Levels> findAllByOrderLevelIn(@Param("orderLevels") Set<Integer> orderLevels);
}
