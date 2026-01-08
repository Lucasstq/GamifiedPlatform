package dev.gamified.GamifiedPlatform.repository;

import dev.gamified.GamifiedPlatform.domain.UserMission;
import dev.gamified.GamifiedPlatform.enums.MissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMissionRepository extends JpaRepository<UserMission, Long> {

    Optional<UserMission> findByUserIdAndMissionId(Long userId, Long missionId);

    List<UserMission> findByUserIdAndStatus(Long userId, MissionStatus status);

    @Query("SELECT um FROM UserMission um " +
           "JOIN FETCH um.mission m " +
           "JOIN FETCH m.level l " +
           "WHERE um.user.id = :userId AND l.id = :levelId " +
           "ORDER BY m.orderNumber ASC")
    List<UserMission> findByUserIdAndLevelId(@Param("userId") Long userId,
                                              @Param("levelId") Long levelId);

    @Query("SELECT COUNT(um) FROM UserMission um " +
           "WHERE um.user.id = :userId " +
           "AND um.mission.level.id = :levelId " +
           "AND um.status = 'CONCLUIDA'")
    Long countCompletedMissionsByUserAndLevel(@Param("userId") Long userId,
                                                @Param("levelId") Long levelId);

    @Query("SELECT um FROM UserMission um " +
           "WHERE um.status = :status " +
           "ORDER BY um.submittedAt ASC")
    Page<UserMission> findAllByStatus(@Param("status") MissionStatus status, Pageable pageable);

    @Query("SELECT um FROM UserMission um " +
           "WHERE um.status IN ('AWAITING_EVALUATION') " +
           "ORDER BY um.submittedAt ASC")
    Page<UserMission> findAllPendingEvaluations(Pageable pageable);

    @Query("SELECT um FROM UserMission um " +
           "WHERE um.evaluatedBy.id = :mentorId " +
           "ORDER BY um.evaluatedAt DESC")
    Page<UserMission> findAllEvaluatedByMentor(@Param("mentorId") Long mentorId, Pageable pageable);

    @Query("SELECT um FROM UserMission um " +
           "WHERE um.evaluatedBy IS NOT NULL " +
           "ORDER BY um.evaluatedAt DESC")
    Page<UserMission> findAllEvaluations(Pageable pageable);

    boolean existsByUserIdAndMissionId(Long userId, Long missionId);
}

