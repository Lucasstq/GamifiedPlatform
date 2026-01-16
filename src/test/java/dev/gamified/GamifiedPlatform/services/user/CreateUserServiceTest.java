package dev.gamified.GamifiedPlatform.services.user;

import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.domain.Scopes;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.request.user.UserRequest;
import dev.gamified.GamifiedPlatform.dtos.response.user.UserResponse;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.repository.ScopeRepository;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import dev.gamified.GamifiedPlatform.services.email.EmailVerificationService;
import dev.gamified.GamifiedPlatform.services.playerCharacter.CreateCharacterForUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateUserService Tests")
class CreateUserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ScopeRepository scopeRepository;
    @Mock
    private CreateCharacterForUserService createCharacterForUser;
    @Mock
    private EmailVerificationService emailVerificationService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private CreateUserService createUserService;

    private UserRequest userRequest;
    private User user;
    private List<Scopes> scopes;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest("user", "user@email.com", "senha123", "User");
        user = User.builder().id(1L).username("user").email("user@email.com").build();
        scopes = List.of(
            Scopes.builder().name("profile:read").build(),
            Scopes.builder().name("profile:write").build(),
            Scopes.builder().name("profile:delete").build(),
            Scopes.builder().name("character:read").build(),
            Scopes.builder().name("character:write").build(),
            Scopes.builder().name("levels:read").build(),
            Scopes.builder().name("quests:read").build(),
            Scopes.builder().name("quests:initiate").build(),
            Scopes.builder().name("quests:complete").build(),
            Scopes.builder().name("achievements:read").build(),
            Scopes.builder().name("bosses:read").build(),
            Scopes.builder().name("bosses:fight").build()
        );
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void shouldCreateUserSuccessfully() {
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.existsByEmail("user@email.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("encoded");
        when(scopeRepository.findByNameIn(any())).thenReturn(scopes);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(createCharacterForUser.execute(any(User.class))).thenReturn(PlayerCharacter.builder().id(1L).build());
        doNothing().when(emailVerificationService).sendVerificationEmail(any(User.class));

        UserResponse response = createUserService.execute(userRequest);
        assertNotNull(response);
        verify(userRepository).save(any(User.class));
        verify(createCharacterForUser).execute(any(User.class));
        verify(emailVerificationService).sendVerificationEmail(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se username já existir")
    void shouldThrowIfUsernameExists() {
        when(userRepository.existsByUsername("user")).thenReturn(true);
        assertThrows(BusinessException.class, () -> createUserService.execute(userRequest));
    }

    @Test
    @DisplayName("Deve lançar exceção se email já existir")
    void shouldThrowIfEmailExists() {
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.existsByEmail("user@email.com")).thenReturn(true);
        assertThrows(BusinessException.class, () -> createUserService.execute(userRequest));
    }

    @Test
    @DisplayName("Deve lançar exceção se escopos não encontrados")
    void shouldThrowIfScopesMissing() {
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.existsByEmail("user@email.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("encoded");
        when(scopeRepository.findByNameIn(any())).thenReturn(List.of());
        Exception ex = assertThrows(IllegalStateException.class, () -> createUserService.execute(userRequest));
        assertTrue(ex.getMessage().contains("Missing scopes"));
    }

    @Test
    @DisplayName("Deve lançar exceção se DataIntegrityViolationException")
    void shouldThrowOnDataIntegrityViolation() {
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.existsByEmail("user@email.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("encoded");
        when(scopeRepository.findByNameIn(any())).thenReturn(scopes);
        when(userRepository.save(any(User.class))).thenThrow(new org.springframework.dao.DataIntegrityViolationException("error"));
        BusinessException ex = assertThrows(BusinessException.class, () -> createUserService.execute(userRequest));
        assertEquals("Username or email already exists", ex.getMessage());
        verify(emailVerificationService, never()).sendVerificationEmail(any(User.class));
    }
}
