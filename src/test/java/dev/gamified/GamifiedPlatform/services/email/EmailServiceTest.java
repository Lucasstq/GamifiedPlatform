package dev.gamified.GamifiedPlatform.services.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService Tests")
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "noreply@gamified.com");
        ReflectionTestUtils.setField(emailService, "baseUrl", "https://gamified.com");
    }

    @Test
    @DisplayName("Deve enviar email de verificação com sucesso")
    void shouldSendVerificationEmailSuccessfully() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        assertDoesNotThrow(() -> emailService.sendVerificationEmail("user@email.com", "user", "token123"));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao falhar envio de email de verificação")
    void shouldThrowOnVerificationEmailFailure() {
        doThrow(new RuntimeException("fail")).when(mailSender).send(any(SimpleMailMessage.class));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> emailService.sendVerificationEmail("user@email.com", "user", "token123"));
        assertTrue(ex.getMessage().contains("Failed to send verification email"));
    }

    @Test
    @DisplayName("Deve enviar email de redefinição de senha com sucesso")
    void shouldSendPasswordResetEmailSuccessfully() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        assertDoesNotThrow(() -> emailService.sendPasswordResetEmail("user@email.com", "user", "token456"));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao falhar envio de email de redefinição de senha")
    void shouldThrowOnPasswordResetEmailFailure() {
        doThrow(new RuntimeException("fail")).when(mailSender).send(any(SimpleMailMessage.class));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> emailService.sendPasswordResetEmail("user@email.com", "user", "token456"));
        assertTrue(ex.getMessage().contains("Failed to send password reset email"));
    }
}

