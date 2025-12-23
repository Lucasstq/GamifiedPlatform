package dev.gamified.GamifiedPlatform.services;

import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.repository.PlayerCharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PlayerCharacterService {

    private final PlayerCharacterRepository playerCharacterRepository;

    public PlayerCharacter createCharacterForUser(User savedUser) {

        PlayerCharacter newPlayerCharacter = PlayerCharacter.builder()
                .name(savedUser.getUsername())
                .level(1)
                .xp(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(savedUser)
                .build();

        return playerCharacterRepository.save(newPlayerCharacter);
    }

    public void deleteCharacter(PlayerCharacter character) {
        playerCharacterRepository.delete(character);
    }

}
