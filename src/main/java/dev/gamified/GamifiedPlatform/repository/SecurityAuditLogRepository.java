package dev.gamified.GamifiedPlatform.repository;

import dev.gamified.GamifiedPlatform.domain.SecurityAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SecurityAuditLogRepository extends JpaRepository<SecurityAuditLog, Long> {

    Page<SecurityAuditLog> findByUserIdOrderByTimestampDesc(Long userId, Pageable pageable);

    Page<SecurityAuditLog> findByEventTypeOrderByTimestampDesc(String eventType, Pageable pageable);

    Page<SecurityAuditLog> findBySeverityOrderByTimestampDesc(String severity, Pageable pageable);

    @Query("SELECT sal FROM SecurityAuditLog sal WHERE sal.timestamp BETWEEN :start AND :end ORDER BY sal.timestamp DESC")
    List<SecurityAuditLog> findByTimestampBetween(@Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end);

    @Query("SELECT sal FROM SecurityAuditLog sal WHERE sal.userId = :userId AND sal.eventType = :eventType ORDER BY sal.timestamp DESC")
    List<SecurityAuditLog> findByUserIdAndEventType(@Param("userId") Long userId,
                                                      @Param("eventType") String eventType);
}

