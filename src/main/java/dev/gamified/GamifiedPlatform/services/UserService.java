package dev.gamified.GamifiedPlatform.services;

import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.request.UserRequest;
import dev.gamified.GamifiedPlatform.dtos.response.UserResponse;
import dev.gamified.GamifiedPlatform.mapper.UserMapper;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PlayerCharacterService playerCharacterService;

    /*
    Ao criar user deve-se criar character associado automaticamente.
     */

    @Transactional
    public UserResponse createUser(UserRequest request) {
        User newUser = UserMapper.toEntity(request);
        User savedUser = userRepository.save(newUser);

        PlayerCharacter characterForUser = playerCharacterService.createCharacterForUser(savedUser);

        savedUser.setPlayerCharacter(characterForUser);

        return UserMapper.toResponse(savedUser);

    }


}
