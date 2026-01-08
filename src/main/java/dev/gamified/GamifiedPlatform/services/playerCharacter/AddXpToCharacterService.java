package dev.gamified.GamifiedPlatform.services.playerCharacter;

import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.dtos.response.levels.LevelResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.PlayerCharacterRepository;
import dev.gamified.GamifiedPlatform.services.levels.CalculateLevelByXpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddXpToCharacterService {

    private final PlayerCharacterRepository characterRepository;
    private final CalculateLevelByXpService calculateLevelByXp;

    /*
     * Adiciona XP ao personagem e recalcular seu nível baseado na tabela tb_levels.
     */
    @Transactional
    public void execute(Long characterId, Integer xpToAdd) {
        log.info("Adding {} XP to character {}", xpToAdd, characterId);

        PlayerCharacter character = characterRepository.findById(characterId)
                .orElseThrow(() -> new ResourceNotFoundException("Character not found with id: " + characterId));

        // Guarda nível anterior para log
        Integer previousLevel = character.getLevel();
        Integer previousXp = character.getXp();

        // Adiciona XP usando o método da entidade
        character.addXp(xpToAdd);

        // Calcula novo nível baseado na tabela tb_levels
        LevelResponse newLevel = calculateLevelByXp.execute(character.getXp());
        character.setLevel(newLevel.orderLevel());

        // Salva alterações
        characterRepository.save(character);

        // Log informativo
        if (!previousLevel.equals(character.getLevel())) {
            log.info("Character {} leveled up! {} -> {} (XP: {} -> {})",
                    characterId, previousLevel, character.getLevel(), previousXp, character.getXp());
        } else {
            log.info("Character {} gained XP: {} -> {}",
                    characterId, previousXp, character.getXp());
        }
    }
}

