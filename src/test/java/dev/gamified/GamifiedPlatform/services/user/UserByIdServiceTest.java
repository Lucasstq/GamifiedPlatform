package dev.gamified.GamifiedPlatform.services.user;

import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.response.user.UserSimpleResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserByIdService Tests")
class UserByIdServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserByIdService userByIdService;

    private User user;
    private PlayerCharacter playerCharacter;

    @BeforeEach
    void setUp() {
        playerCharacter = PlayerCharacter.builder()
                .id(1L)
                .name("Hero")
                .level(5)
                .xp(1500)
                .build();

        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@email.com")
                .avatarUrl("http://avatar.url/image.png")
                .playerCharacter(playerCharacter)
                .build();
    }

    @Test
    @DisplayName("Deve retornar usuário por ID com sucesso")
    void shouldReturnUserByIdSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserSimpleResponse response = userByIdService.execute(1L);

        assertNotNull(response);
        assertEquals("testuser", response.username());
        assertEquals("http://avatar.url/image.png", response.avatarUrl());
        assertNotNull(response.character());
        assertEquals("Hero", response.character().name());
        assertEquals(5, response.character().level());
        assertEquals(1500, response.character().xp());

        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção se usuário não for encontrado")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userByIdService.execute(99L));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(99L);
    }

    @Test
    @DisplayName("Deve retornar dados do personagem associado ao usuário")
    void shouldReturnUserWithCharacterData() {
        playerCharacter.setLevel(10);
        playerCharacter.setXp(5000);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserSimpleResponse response = userByIdService.execute(1L);

        assertNotNull(response.character());
        assertEquals(10, response.character().level());
        assertEquals(5000, response.character().xp());
    }
}

