package dev.gamified.GamifiedPlatform.repository;

import dev.gamified.GamifiedPlatform.domain.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {

    List<Mission> findByLevelIdOrderByOrderNumberAsc(Long levelId);

    @Query("SELECT m FROM Mission m WHERE m.level.id = :levelId ORDER BY m.orderNumber ASC")
    List<Mission> findAllByLevelId(@Param("levelId") Long levelId);

    @Query("SELECT COUNT(m) FROM Mission m WHERE m.level.id = :levelId")
    Long countByLevelId(@Param("levelId") Long levelId);

    boolean existsByLevelIdAndOrderNumber(Long levelId, Integer orderNumber);
}


