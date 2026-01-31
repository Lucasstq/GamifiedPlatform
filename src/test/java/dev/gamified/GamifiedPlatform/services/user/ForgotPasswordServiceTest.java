package dev.gamified.GamifiedPlatform.services.user;

import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.request.user.UserForgetPasswordRequest;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import dev.gamified.GamifiedPlatform.services.email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ForgotPasswordService Tests")
class ForgotPasswordServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ForgotPasswordService forgotPasswordService;

    private User user;
    private UserForgetPasswordRequest request;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@email.com")
                .build();

        request = new UserForgetPasswordRequest("test@email.com");
    }

    @Test
    @DisplayName("Deve enviar email de recuperação de senha com sucesso")
    void shouldSendPasswordResetEmailSuccessfully() {
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString(), anyString());

        assertDoesNotThrow(() -> forgotPasswordService.execute(request));

        verify(userRepository).findByEmail("test@email.com");
        verify(userRepository).save(any(User.class));
        verify(emailService).sendPasswordResetEmail(eq("test@email.com"), eq("testuser"), anyString());
    }

    @Test
    @DisplayName("Deve gerar token de redefinição de senha")
    void shouldGeneratePasswordResetToken() {
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
        when(userRepository.save(userCaptor.capture())).thenReturn(user);
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString(), anyString());

        forgotPasswordService.execute(request);

        User savedUser = userCaptor.getValue();
        assertNotNull(savedUser.getPasswordResetToken());
        assertFalse(savedUser.getPasswordResetToken().isEmpty());
        assertNotNull(savedUser.getPasswordResetTokenExpiresAt());
    }

    @Test
    @DisplayName("Deve definir expiração do token para 1 hora")
    void shouldSetTokenExpirationToOneHour() {
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
        when(userRepository.save(userCaptor.capture())).thenReturn(user);
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString(), anyString());

        forgotPasswordService.execute(request);

        User savedUser = userCaptor.getValue();
        assertNotNull(savedUser.getPasswordResetTokenExpiresAt());
        // Token deve expirar em aproximadamente 1 hora (com margem de alguns segundos)
        assertTrue(savedUser.getPasswordResetTokenExpiresAt().isAfter(java.time.LocalDateTime.now().plusMinutes(59)));
        assertTrue(savedUser.getPasswordResetTokenExpiresAt().isBefore(java.time.LocalDateTime.now().plusMinutes(61)));
    }

    @Test
    @DisplayName("Deve lançar exceção se email não existir")
    void shouldThrowExceptionWhenEmailNotFound() {
        UserForgetPasswordRequest invalidRequest = new UserForgetPasswordRequest("notfound@email.com");
        when(userRepository.findByEmail("notfound@email.com")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> forgotPasswordService.execute(invalidRequest));

        assertEquals("If the email exists, a reset link was sent", exception.getMessage());
        verify(userRepository).findByEmail("notfound@email.com");
        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString(), anyString());
    }
}

