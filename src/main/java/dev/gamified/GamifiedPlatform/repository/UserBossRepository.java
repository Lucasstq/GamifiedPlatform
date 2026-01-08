package dev.gamified.GamifiedPlatform.repository;

import dev.gamified.GamifiedPlatform.domain.UserBoss;
import dev.gamified.GamifiedPlatform.enums.BossFightStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBossRepository extends JpaRepository<UserBoss, Long> {

    Optional<UserBoss> findByUserIdAndBossId(Long userId, Long bossId);

    List<UserBoss> findByUserId(Long userId);

    List<UserBoss> findByUserIdAndStatus(Long userId, BossFightStatus status);

    @Query("SELECT ub FROM UserBoss ub " +
           "JOIN FETCH ub.boss b " +
           "WHERE ub.user.id = :userId AND b.level.id = :levelId")
    Optional<UserBoss> findByUserIdAndLevelId(@Param("userId") Long userId,
                                               @Param("levelId") Long levelId);

    @Query("SELECT ub FROM UserBoss ub " +
           "JOIN FETCH ub.boss " +
           "WHERE ub.status = 'AWAITING_EVALUATION' " +
           "ORDER BY ub.submittedAt ASC")
    Page<UserBoss> findPendingEvaluations(Pageable pageable);

    @Query("SELECT ub FROM UserBoss ub " +
           "WHERE ub.evaluatedBy.id = :mentorId " +
           "ORDER BY ub.evaluatedAt DESC")
    Page<UserBoss> findByEvaluatedById(@Param("mentorId") Long mentorId, Pageable pageable);
}

