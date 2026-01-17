package dev.gamified.GamifiedPlatform.services.playerCharacter;

import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.dtos.response.levels.LevelResponse;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.PlayerCharacterRepository;
import dev.gamified.GamifiedPlatform.services.levels.CalculateLevelByXpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddXpToCharacterServiceTest {

    @Mock
    private PlayerCharacterRepository characterRepository;

    @Mock
    private CalculateLevelByXpService calculateLevelByXp;

    private AddXpToCharacterService service;

    @BeforeEach
    void setUp() {
        service = new AddXpToCharacterService(characterRepository, calculateLevelByXp);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando personagem não existir")
    void execute_shouldThrowResourceNotFound_whenCharacterNotFound() {
        Long characterId = 1L;

        when(characterRepository.findById(characterId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.execute(characterId, 100));

        assertTrue(ex.getMessage().contains("Character not found"));

        verify(characterRepository).findById(characterId);
        verifyNoInteractions(calculateLevelByXp);
        verify(characterRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando XP a adicionar for nulo")
    void execute_shouldThrowBusinessException_whenXpToAddIsNull() {
        Long characterId = 1L;
        PlayerCharacter character = baseCharacter(10, 100);

        when(characterRepository.findById(characterId)).thenReturn(Optional.of(character));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.execute(characterId, null));

        assertEquals("XP to add must be a positive number", ex.getMessage());

        verify(characterRepository).findById(characterId);
        verifyNoInteractions(calculateLevelByXp);
        verify(characterRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando XP a adicionar for negativo")
    void execute_shouldThrowBusinessException_whenXpToAddIsNegative() {
        Long characterId = 1L;
        PlayerCharacter character = baseCharacter(10, 100);

        when(characterRepository.findById(characterId)).thenReturn(Optional.of(character));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.execute(characterId, -1));

        assertEquals("XP to add must be a positive number", ex.getMessage());

        verify(characterRepository).findById(characterId);
        verifyNoInteractions(calculateLevelByXp);
        verify(characterRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve adicionar XP, recalcular nível e salvar personagem")
    void execute_shouldAddXpRecalculateLevelAndSave() {
        Long characterId = 1L;
        PlayerCharacter character = baseCharacter(3, 50);

        when(characterRepository.findById(characterId)).thenReturn(Optional.of(character));

        // novo XP = 50 + 30 = 80
        LevelResponse levelResponse = mock(LevelResponse.class);
        when(levelResponse.orderLevel()).thenReturn(4);
        when(calculateLevelByXp.execute(80)).thenReturn(levelResponse);

        service.execute(characterId, 30);

        assertEquals(80, character.getXp());
        assertEquals(4, character.getLevel());

        verify(characterRepository).findById(characterId);

        // garante que o cálculo recebeu o XP já atualizado
        verify(calculateLevelByXp).execute(80);

        // garante que salvou o mesmo personagem alterado
        ArgumentCaptor<PlayerCharacter> captor = ArgumentCaptor.forClass(PlayerCharacter.class);
        verify(characterRepository).save(captor.capture());
        assertSame(character, captor.getValue());
    }

    @Test
    @DisplayName("Deve manter nível quando cálculo retornar o mesmo nível")
    void execute_shouldKeepSameLevel_whenCalculatedLevelIsSame() {
        Long characterId = 1L;
        PlayerCharacter character = baseCharacter(3, 50);

        when(characterRepository.findById(characterId)).thenReturn(Optional.of(character));

        // novo XP = 70, mas level continua 3
        LevelResponse levelResponse = mock(LevelResponse.class);
        when(levelResponse.orderLevel()).thenReturn(3);
        when(calculateLevelByXp.execute(70)).thenReturn(levelResponse);

        service.execute(characterId, 20);

        assertEquals(70, character.getXp());
        assertEquals(3, character.getLevel());

        verify(calculateLevelByXp).execute(70);
        verify(characterRepository).save(character);
    }

    private PlayerCharacter baseCharacter(int level, int xp) {
        PlayerCharacter c = new PlayerCharacter();
        c.setId(1L);
        c.setName("Arthas");
        c.setLevel(level);
        c.setXp(xp);
        return c;
    }
}

