package dev.gamified.GamifiedPlatform.services.user;

import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.exceptions.InvalidPasswordException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import dev.gamified.GamifiedPlatform.config.security.PermissionValidator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteUserService Tests")
class DeleteUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DeleteUserService deleteUserService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@email.com")
                .password("encodedPassword")
                .deleted(false)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso (soft delete)")
    void shouldDeleteUserSuccessfully() {
        try (MockedStatic<PermissionValidator> mockedValidator = mockStatic(PermissionValidator.class)) {
            mockedValidator.when(() -> PermissionValidator.validateResourceOwnerOrAdmin(1L)).thenAnswer(invocation -> null);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("correctPassword", "encodedPassword")).thenReturn(true);
            when(userRepository.save(any(User.class))).thenReturn(user);

            assertDoesNotThrow(() -> deleteUserService.execute(1L, "correctPassword"));

            verify(userRepository).findById(1L);
            verify(passwordEncoder).matches("correctPassword", "encodedPassword");
            verify(userRepository).save(any(User.class));
        }
    }

    @Test
    @DisplayName("Deve lançar exceção se usuário não for encontrado")
    void shouldThrowExceptionWhenUserNotFound() {
        try (MockedStatic<PermissionValidator> mockedValidator = mockStatic(PermissionValidator.class)) {
            mockedValidator.when(() -> PermissionValidator.validateResourceOwnerOrAdmin(1L)).thenAnswer(invocation -> null);

            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> deleteUserService.execute(1L, "password"));

            verify(userRepository).findById(1L);
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Test
    @DisplayName("Deve lançar exceção se usuário já foi deletado")
    void shouldThrowExceptionWhenUserAlreadyDeleted() {
        try (MockedStatic<PermissionValidator> mockedValidator = mockStatic(PermissionValidator.class)) {
            mockedValidator.when(() -> PermissionValidator.validateResourceOwnerOrAdmin(1L)).thenAnswer(invocation -> null);

            user.setDeleted(true);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> deleteUserService.execute(1L, "password"));

            assertEquals("User already deleted", exception.getMessage());
            verify(userRepository).findById(1L);
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Test
    @DisplayName("Deve lançar exceção se senha estiver incorreta")
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        try (MockedStatic<PermissionValidator> mockedValidator = mockStatic(PermissionValidator.class)) {
            mockedValidator.when(() -> PermissionValidator.validateResourceOwnerOrAdmin(1L)).thenAnswer(invocation -> null);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

            assertThrows(InvalidPasswordException.class,
                    () -> deleteUserService.execute(1L, "wrongPassword"));

            verify(userRepository).findById(1L);
            verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
            verify(userRepository, never()).save(any(User.class));
        }
    }
}

