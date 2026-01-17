package dev.gamified.GamifiedPlatform.services.user;

import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.request.user.UserChangePasswordRequest;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResetPasswordConfirmServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private ResetPasswordConfirmService service;

    @BeforeEach
    void setUp() {
        service = new ResetPasswordConfirmService(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando token for inválido")
    void execute_shouldThrowBusinessException_whenTokenInvalid() {
        String token = "invalid-token";
        UserChangePasswordRequest request = mock(UserChangePasswordRequest.class);

        when(userRepository.findByPasswordResetToken(token)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.execute(token, request));

        assertEquals("Invalid token", ex.getMessage());
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando token estiver expirado")
    void execute_shouldThrowBusinessException_whenTokenExpired() {
        String token = "token";
        User user = new User();
        user.setPassword("encoded-old");
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiresAt(LocalDateTime.now().minusMinutes(1));

        UserChangePasswordRequest request = mock(UserChangePasswordRequest.class);

        when(userRepository.findByPasswordResetToken(token)).thenReturn(Optional.of(user));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.execute(token, request));

        assertEquals("Token has expired", ex.getMessage());
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando expiração do token for nula")
    void execute_shouldThrowBusinessException_whenTokenExpiryIsNull() {
        String token = "token";
        User user = new User();
        user.setPassword("encoded-old");
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiresAt(null);

        UserChangePasswordRequest request = mock(UserChangePasswordRequest.class);

        when(userRepository.findByPasswordResetToken(token)).thenReturn(Optional.of(user));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.execute(token, request));

        assertEquals("Token has expired", ex.getMessage());
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando confirmação de senha não coincidir")
    void execute_shouldThrowBusinessException_whenPasswordConfirmationDoesNotMatch() {
        String token = "token";
        User user = new User();
        user.setPassword("encoded-old");
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiresAt(LocalDateTime.now().plusMinutes(10));

        UserChangePasswordRequest request = mock(UserChangePasswordRequest.class);
        when(request.newPassword()).thenReturn("newPass");
        when(request.confirmNewPassword()).thenReturn("differentPass");

        when(userRepository.findByPasswordResetToken(token)).thenReturn(Optional.of(user));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.execute(token, request));

        assertEquals("Password confirmation does not match", ex.getMessage());
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando nova senha for igual à atual")
    void execute_shouldThrowBusinessException_whenNewPasswordEqualsOldPassword() {
        String token = "token";
        User user = new User();
        user.setPassword("encoded-old");
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiresAt(LocalDateTime.now().plusMinutes(10));

        UserChangePasswordRequest request = mock(UserChangePasswordRequest.class);
        when(request.newPassword()).thenReturn("samePass");
        when(request.confirmNewPassword()).thenReturn("samePass");

        when(userRepository.findByPasswordResetToken(token)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("samePass", "encoded-old")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.execute(token, request));

        assertEquals("New password must be different", ex.getMessage());
        verify(userRepository, never()).save(any());
        verify(passwordEncoder).matches("samePass", "encoded-old");
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Deve resetar a senha com sucesso e limpar token e expiração")
    void execute_shouldResetPasswordSuccessfully() {
        String token = "token";
        User user = new User();
        user.setPassword("encoded-old");
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiresAt(LocalDateTime.now().plusMinutes(10));

        UserChangePasswordRequest request = mock(UserChangePasswordRequest.class);
        when(request.newPassword()).thenReturn("newPass");
        when(request.confirmNewPassword()).thenReturn("newPass");

        when(userRepository.findByPasswordResetToken(token)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("newPass", "encoded-old")).thenReturn(false);
        when(passwordEncoder.encode("newPass")).thenReturn("encoded-new");

        service.execute(token, request);

        assertEquals("encoded-new", user.getPassword());
        assertNull(user.getPasswordResetToken());
        assertNull(user.getPasswordResetTokenExpiresAt());

        verify(passwordEncoder).encode("newPass");
        verify(userRepository).save(user);
    }
}

