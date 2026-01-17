package dev.gamified.GamifiedPlatform.services.user;

import dev.gamified.GamifiedPlatform.config.security.PermissionValidator;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.request.user.UserAuthenticateChangePasswordRequest;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserChangePasswordServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserChangePasswordService service;

    @BeforeEach
    void setUp() {
        service = new UserChangePasswordService(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando usuário não existir")
    void execute_shouldThrowResourceNotFound_whenUserNotFound() {
        Long userId = 1L;

        UserAuthenticateChangePasswordRequest request =
                new UserAuthenticateChangePasswordRequest("newPass", "newPass", "newPass");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        try (MockedStatic<PermissionValidator> mocked =
                     mockStatic(PermissionValidator.class)) {

            mocked.when(() ->
                            PermissionValidator.validateResourceOwnerOrAdmin(userId))
                    .thenAnswer(invocation -> null);

            assertThrows(ResourceNotFoundException.class,
                    () -> service.execute(userId, request));
        }

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando confirmação de senha não coincidir")
    void execute_shouldThrowBusinessException_whenPasswordConfirmationDoesNotMatch() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setPassword("encoded-old");

        UserAuthenticateChangePasswordRequest request =
                new UserAuthenticateChangePasswordRequest("oldPass", "newPass", "differentPass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        try (MockedStatic<PermissionValidator> mocked =
                     mockStatic(PermissionValidator.class)) {

            mocked.when(() ->
                            PermissionValidator.validateResourceOwnerOrAdmin(userId))
                    .thenAnswer(invocation -> null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.execute(userId, request));

            assertEquals("Password confirmation does not match", ex.getMessage());
        }

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando nova senha for igual à senha atual")
    void execute_shouldThrowBusinessException_whenNewPasswordEqualsOldPassword() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setPassword("encoded-old");

        UserAuthenticateChangePasswordRequest request =
                new UserAuthenticateChangePasswordRequest("samePass", "samePass", "samePass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("samePass", "encoded-old")).thenReturn(true);

        try (MockedStatic<PermissionValidator> mocked =
                     mockStatic(PermissionValidator.class)) {

            mocked.when(() ->
                            PermissionValidator.validateResourceOwnerOrAdmin(userId))
                    .thenAnswer(invocation -> null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.execute(userId, request));

            assertEquals(
                    "New password must be different from the current password",
                    ex.getMessage()
            );
        }

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve alterar senha quando dados forem válidos")
    void execute_shouldChangePasswordSuccessfully() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setPassword("encoded-old");

        UserAuthenticateChangePasswordRequest request =
                new UserAuthenticateChangePasswordRequest("newPass", "newPass", "newPass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("newPass", "encoded-old")).thenReturn(false);
        when(passwordEncoder.encode("newPass")).thenReturn("encoded-new");

        try (MockedStatic<PermissionValidator> mocked =
                     mockStatic(PermissionValidator.class)) {

            mocked.when(() ->
                            PermissionValidator.validateResourceOwnerOrAdmin(userId))
                    .thenAnswer(invocation -> null);

            service.execute(userId, request);
        }

        assertEquals("encoded-new", user.getPassword());
        verify(userRepository).save(user);
    }
}

