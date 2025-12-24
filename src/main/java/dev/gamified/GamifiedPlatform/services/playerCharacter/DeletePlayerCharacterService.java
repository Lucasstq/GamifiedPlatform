package dev.gamified.GamifiedPlatform.services.playerCharacter;

import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.repository.PlayerCharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeletePlayerCharacterService {

    private final PlayerCharacterRepository playerCharacterRepository;

    public void execute(PlayerCharacter character) {
        playerCharacterRepository.delete(character);
    }

}
