package dev.gamified.GamifiedPlatform.services.user;

import dev.gamified.GamifiedPlatform.config.security.PermissionValidator;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.request.user.UserUpdateRequest;
import dev.gamified.GamifiedPlatform.dtos.response.user.UserResponse;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UpdateUserService service;

    @BeforeEach
    void setUp() {
        service = new UpdateUserService(userRepository);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando usuário não existir")
    void execute_shouldThrowResourceNotFound_whenUserNotFound() {
        Long userId = 1L;
        UserUpdateRequest request = mock(UserUpdateRequest.class);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        try (MockedStatic<PermissionValidator> mocked = mockStatic(PermissionValidator.class)) {
            mocked.when(() -> PermissionValidator.validateResourceOwnerOrAdmin(userId))
                    .thenAnswer(inv -> null);

            assertThrows(ResourceNotFoundException.class, () -> service.execute(userId, request));
        }

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando username mudar e já existir")
    void execute_shouldThrowBusinessException_whenUsernameAlreadyExists() {
        Long userId = 1L;

        User existing = new User();
        existing.setId(userId);
        existing.setUsername("old");
        existing.setEmail("old@email.com");

        UserUpdateRequest request = mock(UserUpdateRequest.class);
        when(request.username()).thenReturn("new");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existing));
        when(userRepository.existsByUsername("new")).thenReturn(true);

        try (MockedStatic<PermissionValidator> mocked = mockStatic(PermissionValidator.class)) {
            mocked.when(() -> PermissionValidator.validateResourceOwnerOrAdmin(userId))
                    .thenAnswer(inv -> null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.execute(userId, request));

            assertEquals("Username already exists", ex.getMessage());
        }

        verify(userRepository).existsByUsername("new");
        verify(userRepository, never()).save(any());
        verify(userRepository, never()).existsByEmail(anyString());
    }


    @Test
    @DisplayName("Deve lançar BusinessException quando email mudar e já existir")
    void execute_shouldThrowBusinessException_whenEmailAlreadyExists() {
        Long userId = 1L;

        User existing = new User();
        existing.setId(userId);
        existing.setUsername("same");
        existing.setEmail("old@email.com");

        UserUpdateRequest request = mock(UserUpdateRequest.class);
        when(request.username()).thenReturn("same");
        when(request.email()).thenReturn("new@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail("new@email.com")).thenReturn(true);

        try (MockedStatic<PermissionValidator> mocked = mockStatic(PermissionValidator.class)) {
            mocked.when(() -> PermissionValidator.validateResourceOwnerOrAdmin(userId))
                    .thenAnswer(inv -> null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.execute(userId, request));

            assertEquals("Email already exists", ex.getMessage());
        }

        verify(userRepository).existsByEmail("new@email.com");
        verify(userRepository, never()).save(any());
        verify(userRepository, never()).existsByUsername(anyString());
    }


    @Test
    @DisplayName("Não deve checar unicidade quando username e email não mudarem")
    void execute_shouldNotCheckUniqueness_whenUsernameAndEmailUnchanged() {
        Long userId = 1L;

        User existing = new User();
        existing.setId(userId);
        existing.setUsername("same");
        existing.setEmail("same@email.com");
        existing.setAvatarUrl("old-avatar");

        UserUpdateRequest request = mock(UserUpdateRequest.class);
        when(request.username()).thenReturn("same");
        when(request.email()).thenReturn("same@email.com"); // igual
        when(request.avatarUrl()).thenReturn("new-avatar");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        try (MockedStatic<PermissionValidator> mocked = mockStatic(PermissionValidator.class)) {
            mocked.when(() -> PermissionValidator.validateResourceOwnerOrAdmin(userId))
                    .thenAnswer(inv -> null);

            // Não validar conteúdo do response aqui pra não depender do UserMapper
            UserResponse response = service.execute(userId, request);
            assertNotNull(response);
        }

        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository).save(existing);

        assertEquals("new-avatar", existing.getAvatarUrl());
        assertEquals("same", existing.getUsername());
        assertEquals("same@email.com", existing.getEmail());
    }

    @Test
    @DisplayName("Deve atualizar username, email e avatar e salvar usuário")
    void execute_shouldUpdateAndSaveUser() {
        Long userId = 1L;

        User existing = new User();
        existing.setId(userId);
        existing.setUsername("old");
        existing.setEmail("old@email.com");

        UserUpdateRequest request = mock(UserUpdateRequest.class);
        when(request.username()).thenReturn("new");
        when(request.email()).thenReturn("new@email.com");
        when(request.avatarUrl()).thenReturn("avatar");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existing));
        when(userRepository.existsByUsername("new")).thenReturn(false);
        when(userRepository.existsByEmail("new@email.com")).thenReturn(false);

        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        try (MockedStatic<PermissionValidator> mocked = mockStatic(PermissionValidator.class)) {
            mocked.when(() -> PermissionValidator.validateResourceOwnerOrAdmin(userId))
                    .thenAnswer(inv -> null);

            UserResponse response = service.execute(userId, request);
            assertNotNull(response);
        }

        verify(userRepository).existsByUsername("new");
        verify(userRepository).existsByEmail("new@email.com");
        verify(userRepository).save(existing);

        assertEquals("new", existing.getUsername());
        assertEquals("new@email.com", existing.getEmail());
        assertEquals("avatar", existing.getAvatarUrl());
    }
}

