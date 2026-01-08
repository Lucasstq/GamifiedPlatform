package dev.gamified.GamifiedPlatform.repository;

import dev.gamified.GamifiedPlatform.domain.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    @Query("SELECT ub FROM UserBadge ub " +
           "JOIN FETCH ub.badge b " +
           "JOIN FETCH b.level l " +
           "WHERE ub.user.id = :userId " +
           "ORDER BY ub.unlockedAt DESC")
    List<UserBadge> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT ub FROM UserBadge ub WHERE ub.user.id = :userId AND ub.badge.id = :badgeId")
    Optional<UserBadge> findByUserIdAndBadgeId(@Param("userId") Long userId, @Param("badgeId") Long badgeId);

    @Query("SELECT COUNT(ub) FROM UserBadge ub WHERE ub.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(ub) > 0 THEN true ELSE false END FROM UserBadge ub " +
           "WHERE ub.user.id = :userId AND ub.badge.id = :badgeId")
    Boolean existsByUserIdAndBadgeId(@Param("userId") Long userId, @Param("badgeId") Long badgeId);
}

