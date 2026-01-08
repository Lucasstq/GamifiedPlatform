package dev.gamified.GamifiedPlatform.services.security;

import dev.gamified.GamifiedPlatform.domain.SecurityAuditLog;
import dev.gamified.GamifiedPlatform.repository.SecurityAuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Serviço para logs de auditoria de segurança.
 * Rastreia eventos críticos para detecção de ameaças e investigação.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityAuditService {

    private final SecurityAuditLogRepository auditLogRepository;

    @Async
    public void logLoginSuccess(Long userId, String username, String ipAddress, String userAgent) {
        SecurityAuditLog auditLog = SecurityAuditLog.builder()
                .eventType("LOGIN_SUCCESS")
                .userId(userId)
                .username(username)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .severity("INFO")
                .details("User logged in successfully")
                .build();

        auditLogRepository.save(auditLog);
        log.info("AUDIT: Login successful - user: {}, ip: {}", username, ipAddress);
    }

    @Async
    public void logLoginFailure(String username, String ipAddress, String userAgent, String reason) {
        SecurityAuditLog auditLog = SecurityAuditLog.builder()
                .eventType("LOGIN_FAILED")
                .username(username)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .severity("WARNING")
                .details("Login failed: " + reason)
                .build();

        auditLogRepository.save(auditLog);
        log.warn("AUDIT: Login failed - user: {}, ip: {}, reason: {}", username, ipAddress, reason);
    }

    @Async
    public void logPasswordChange(Long userId, String username, String ipAddress) {
        SecurityAuditLog auditLog = SecurityAuditLog.builder()
                .eventType("PASSWORD_CHANGED")
                .userId(userId)
                .username(username)
                .ipAddress(ipAddress)
                .severity("INFO")
                .details("Password changed successfully")
                .build();

        auditLogRepository.save(auditLog);
        log.info("AUDIT: Password changed - user: {}, ip: {}", username, ipAddress);
    }

    @Async
    public void logLogout(Long userId, String username, String ipAddress) {
        SecurityAuditLog auditLog = SecurityAuditLog.builder()
                .eventType("LOGOUT")
                .userId(userId)
                .username(username)
                .ipAddress(ipAddress)
                .severity("INFO")
                .details("User logged out")
                .build();

        auditLogRepository.save(auditLog);
        log.info("AUDIT: Logout - user: {}, ip: {}", username, ipAddress);
    }

    @Async
    public void logUnauthorizedAccess(Long userId, String username, String ipAddress, String resource) {
        SecurityAuditLog auditLog = SecurityAuditLog.builder()
                .eventType("UNAUTHORIZED_ACCESS")
                .userId(userId)
                .username(username)
                .ipAddress(ipAddress)
                .severity("CRITICAL")
                .details("Attempted to access unauthorized resource: " + resource)
                .build();

        auditLogRepository.save(auditLog);
        log.error("AUDIT: Unauthorized access attempt - user: {}, ip: {}, resource: {}",
                  username, ipAddress, resource);
    }

    @Async
    public void logAccountCreated(Long userId, String username, String ipAddress) {
        SecurityAuditLog auditLog = SecurityAuditLog.builder()
                .eventType("ACCOUNT_CREATED")
                .userId(userId)
                .username(username)
                .ipAddress(ipAddress)
                .severity("INFO")
                .details("New account created")
                .build();

        auditLogRepository.save(auditLog);
        log.info("AUDIT: Account created - user: {}, ip: {}", username, ipAddress);
    }

    @Async
    public void logAccountDeleted(Long userId, String username, String ipAddress) {
        SecurityAuditLog auditLog = SecurityAuditLog.builder()
                .eventType("ACCOUNT_DELETED")
                .userId(userId)
                .username(username)
                .ipAddress(ipAddress)
                .severity("WARNING")
                .details("Account deleted")
                .build();

        auditLogRepository.save(auditLog);
        log.warn("AUDIT: Account deleted - user: {}, ip: {}", username, ipAddress);
    }

    @Async
    public void logTokenRefresh(Long userId, String username, String ipAddress) {
        SecurityAuditLog auditLog = SecurityAuditLog.builder()
                .eventType("TOKEN_REFRESHED")
                .userId(userId)
                .username(username)
                .ipAddress(ipAddress)
                .severity("INFO")
                .details("Access token refreshed")
                .build();

        auditLogRepository.save(auditLog);
        log.debug("AUDIT: Token refreshed - user: {}, ip: {}", username, ipAddress);
    }

    @Async
    public void logSuspiciousActivity(Long userId, String username, String ipAddress, String details) {
        SecurityAuditLog auditLog = SecurityAuditLog.builder()
                .eventType("SUSPICIOUS_ACTIVITY")
                .userId(userId)
                .username(username)
                .ipAddress(ipAddress)
                .severity("CRITICAL")
                .details(details)
                .build();

        auditLogRepository.save(auditLog);
        log.error("AUDIT: Suspicious activity detected - user: {}, ip: {}, details: {}",
                  username, ipAddress, details);
    }
}

