package dev.gamified.GamifiedPlatform.services.playerCharacter;

import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.repository.PlayerCharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateCharacterForUserService {

    private final PlayerCharacterRepository playerCharacterRepository;

    public PlayerCharacter execute(User savedUser) {

        PlayerCharacter newPlayerCharacter = PlayerCharacter.builder()
                .name(savedUser.getUsername())
                .level(1)
                .xp(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(savedUser)
                .build();

        savedUser.setPlayerCharacter(newPlayerCharacter);

        return playerCharacterRepository.save(newPlayerCharacter);
    }

}
