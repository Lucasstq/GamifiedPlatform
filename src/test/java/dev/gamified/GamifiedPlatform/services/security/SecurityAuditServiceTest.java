package dev.gamified.GamifiedPlatform.services.security;

import dev.gamified.GamifiedPlatform.domain.SecurityAuditLog;
import dev.gamified.GamifiedPlatform.repository.SecurityAuditLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityAuditService Tests")
class SecurityAuditServiceTest {

    @Mock
    private SecurityAuditLogRepository auditLogRepository;

    @InjectMocks
    private SecurityAuditService securityAuditService;

    private static final Long USER_ID = 1L;
    private static final String USERNAME = "testuser";
    private static final String IP_ADDRESS = "192.168.1.1";
    private static final String USER_AGENT = "Mozilla/5.0";

    @Test
    @DisplayName("Deve registrar log de login bem-sucedido")
    void shouldLogLoginSuccess() {
        ArgumentCaptor<SecurityAuditLog> logCaptor = ArgumentCaptor.forClass(SecurityAuditLog.class);
        when(auditLogRepository.save(logCaptor.capture())).thenReturn(new SecurityAuditLog());

        securityAuditService.logLoginSuccess(USER_ID, USERNAME, IP_ADDRESS, USER_AGENT);

        SecurityAuditLog savedLog = logCaptor.getValue();
        assertEquals("LOGIN_SUCCESS", savedLog.getEventType());
        assertEquals(USER_ID, savedLog.getUserId());
        assertEquals(USERNAME, savedLog.getUsername());
        assertEquals(IP_ADDRESS, savedLog.getIpAddress());
        assertEquals(USER_AGENT, savedLog.getUserAgent());
        assertEquals("INFO", savedLog.getSeverity());
        assertEquals("User logged in successfully", savedLog.getDetails());
    }

    @Test
    @DisplayName("Deve registrar log de falha no login")
    void shouldLogLoginFailure() {
        ArgumentCaptor<SecurityAuditLog> logCaptor = ArgumentCaptor.forClass(SecurityAuditLog.class);
        when(auditLogRepository.save(logCaptor.capture())).thenReturn(new SecurityAuditLog());

        securityAuditService.logLoginFailure(USERNAME, IP_ADDRESS, USER_AGENT, "Invalid password");

        SecurityAuditLog savedLog = logCaptor.getValue();
        assertEquals("LOGIN_FAILED", savedLog.getEventType());
        assertNull(savedLog.getUserId());
        assertEquals(USERNAME, savedLog.getUsername());
        assertEquals(IP_ADDRESS, savedLog.getIpAddress());
        assertEquals(USER_AGENT, savedLog.getUserAgent());
        assertEquals("WARNING", savedLog.getSeverity());
        assertEquals("Login failed: Invalid password", savedLog.getDetails());
    }

    @Test
    @DisplayName("Deve registrar log de alteração de senha")
    void shouldLogPasswordChange() {
        ArgumentCaptor<SecurityAuditLog> logCaptor = ArgumentCaptor.forClass(SecurityAuditLog.class);
        when(auditLogRepository.save(logCaptor.capture())).thenReturn(new SecurityAuditLog());

        securityAuditService.logPasswordChange(USER_ID, USERNAME, IP_ADDRESS);

        SecurityAuditLog savedLog = logCaptor.getValue();
        assertEquals("PASSWORD_CHANGED", savedLog.getEventType());
        assertEquals(USER_ID, savedLog.getUserId());
        assertEquals(USERNAME, savedLog.getUsername());
        assertEquals(IP_ADDRESS, savedLog.getIpAddress());
        assertEquals("INFO", savedLog.getSeverity());
        assertEquals("Password changed successfully", savedLog.getDetails());
    }

    @Test
    @DisplayName("Deve registrar log de logout")
    void shouldLogLogout() {
        ArgumentCaptor<SecurityAuditLog> logCaptor = ArgumentCaptor.forClass(SecurityAuditLog.class);
        when(auditLogRepository.save(logCaptor.capture())).thenReturn(new SecurityAuditLog());

        securityAuditService.logLogout(USER_ID, USERNAME, IP_ADDRESS);

        SecurityAuditLog savedLog = logCaptor.getValue();
        assertEquals("LOGOUT", savedLog.getEventType());
        assertEquals(USER_ID, savedLog.getUserId());
        assertEquals(USERNAME, savedLog.getUsername());
        assertEquals(IP_ADDRESS, savedLog.getIpAddress());
        assertEquals("INFO", savedLog.getSeverity());
        assertEquals("User logged out", savedLog.getDetails());
    }

    @Test
    @DisplayName("Deve registrar log de logout de todos os dispositivos")
    void shouldLogLogoutAllDevices() {
        ArgumentCaptor<SecurityAuditLog> logCaptor = ArgumentCaptor.forClass(SecurityAuditLog.class);
        when(auditLogRepository.save(logCaptor.capture())).thenReturn(new SecurityAuditLog());

        securityAuditService.logLogoutAllDevices(USER_ID, USERNAME, IP_ADDRESS);

        SecurityAuditLog savedLog = logCaptor.getValue();
        assertEquals("LOGOUT_ALL_DEVICES", savedLog.getEventType());
        assertEquals(USER_ID, savedLog.getUserId());
        assertEquals(USERNAME, savedLog.getUsername());
        assertEquals(IP_ADDRESS, savedLog.getIpAddress());
        assertEquals("WARNING", savedLog.getSeverity());
        assertEquals("User logged out from all devices", savedLog.getDetails());
    }

    @Test
    @DisplayName("Deve registrar log de acesso não autorizado")
    void shouldLogUnauthorizedAccess() {
        ArgumentCaptor<SecurityAuditLog> logCaptor = ArgumentCaptor.forClass(SecurityAuditLog.class);
        when(auditLogRepository.save(logCaptor.capture())).thenReturn(new SecurityAuditLog());

        securityAuditService.logUnauthorizedAccess(USER_ID, USERNAME, IP_ADDRESS, "/api/admin/users");

        SecurityAuditLog savedLog = logCaptor.getValue();
        assertEquals("UNAUTHORIZED_ACCESS", savedLog.getEventType());
        assertEquals(USER_ID, savedLog.getUserId());
        assertEquals(USERNAME, savedLog.getUsername());
        assertEquals(IP_ADDRESS, savedLog.getIpAddress());
    }

    @Test
    @DisplayName("Deve salvar log no repositório")
    void shouldSaveLogToRepository() {
        when(auditLogRepository.save(any(SecurityAuditLog.class))).thenReturn(new SecurityAuditLog());

        securityAuditService.logLoginSuccess(USER_ID, USERNAME, IP_ADDRESS, USER_AGENT);

        verify(auditLogRepository).save(any(SecurityAuditLog.class));
    }
}

