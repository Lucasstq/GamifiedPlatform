package dev.gamified.GamifiedPlatform.services.user;

import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.request.UserRequest;
import dev.gamified.GamifiedPlatform.dtos.response.UserResponse;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.mapper.UserMapper;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import dev.gamified.GamifiedPlatform.services.playerCharacter.CreateCharacterForUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateUserService {

    private final UserRepository userRepository;
    private final CreateCharacterForUserService createCharacterForUser;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse execute(UserRequest request) {

        validateUserDoesNotExist(request);

        User newUser = UserMapper.toEntity(request);
        newUser.setPassword(passwordEncoder.encode(request.password()));

        try {
            User savedUser = userRepository.save(newUser);
            // Cria personagem associado ao usuário
            createCharacterForUser.execute(savedUser);
            return UserMapper.toResponse(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Username or email already exists");
        }

    }

    private void validateUserDoesNotExist(UserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already exists");
        }

    }

}
