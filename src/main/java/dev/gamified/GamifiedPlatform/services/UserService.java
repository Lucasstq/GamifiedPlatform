package dev.gamified.GamifiedPlatform.services;

import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.request.UserRequest;
import dev.gamified.GamifiedPlatform.dtos.response.UserResponse;
import dev.gamified.GamifiedPlatform.exceptions.InvalidPasswordException;
import dev.gamified.GamifiedPlatform.exceptions.ResourseNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.UserMapper;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PlayerCharacterService playerCharacterService;
    private final PasswordEncoder passwordEncoder;

    /*
    TODO:Adicionar regras de negocio para criação de user.
    como por exemplo: não permitir criação de user com email já existente.
    ou não permitir criação de user com username já existente.
     */

    @Transactional
    public UserResponse createUser(UserRequest request) {
        User newUser = UserMapper.toEntity(request);
        //Hash da senha
        newUser.setPassword(passwordEncoder.encode(request.password()));
        User savedUser = userRepository.save(newUser);

        // Cria personagem associado ao usuário
        PlayerCharacter characterForUser = playerCharacterService.createCharacterForUser(savedUser);

        savedUser.setPlayerCharacter(characterForUser);

        return UserMapper.toResponse(savedUser);

    }

    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourseNotFoundException("User not found"));
        return UserMapper.toResponse(user);
    }


    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourseNotFoundException("User not found"));
        return UserMapper.toResponse(user);
    }


    /*
    TODO:Adicionar regras de negocio para atualização de user.
    como por exemplo: não permitir alteração de email para um email já existente.
    ou não permitir alteração de senha sem confirmação da senha antiga.
    e também não permitir alteração de username para um username já existente.
     */

    public UserResponse updateUser(Long userId, UserRequest request) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourseNotFoundException("User not found"));

        existingUser.setUsername(request.username());
        existingUser.setEmail(request.email());
        // Hash da senha se ela for fornecida
        if (!request.password().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.password()));
        }
        existingUser.setAvatarUrl(request.avatarUrl());

        User updatedUser = userRepository.save(existingUser);
        return UserMapper.toResponse(updatedUser);
    }


    @Transactional
    public void deleteUser(Long userId, String password) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourseNotFoundException("User not found"));

        // Validar se a senha fornecida está correta
        if (!passwordEncoder.matches(password, existingUser.getPassword())) {
            throw new InvalidPasswordException("Invalid password. Cannot delete user.");
        }

        // Deletar o personagem associado se existir
        if(existingUser.getPlayerCharacter() != null) {
            playerCharacterService.deleteCharacter(existingUser.getPlayerCharacter());
        }

        userRepository.delete(existingUser);
    }


}
