package dev.gamified.GamifiedPlatform.services.user;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.request.user.UserUpdateRequest;
import dev.gamified.GamifiedPlatform.dtos.response.UserResponse;
import dev.gamified.GamifiedPlatform.exceptions.AcessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourseNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.UserMapper;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateUserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse execute(Long userId, UserUpdateRequest request) {

        isOwnerOrAdmin(userId);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourseNotFoundException("User not found"));

        validateUniqueFields(existingUser, request);

        existingUser.setUsername(request.username());
        existingUser.setEmail(request.email());
        existingUser.setAvatarUrl(request.avatarUrl());

        User updatedUser = userRepository.save(existingUser);
        return UserMapper.toResponse(updatedUser);
    }

    // Verifica se o usuário autenticado tem permissão para atualizar
    private void isOwnerOrAdmin(Long userId) {
        if (!SecurityUtils.isResourceOwnerOrAdmin(userId)) {
            throw new AcessDeniedException("You do not have permission to update this user");
        }
    }


    /*
    Verifica se o username ou email foram alterados e se já existem no banco de dados
    e também se são diferentes dos atuais.
   */
    private void validateUniqueFields(User currentUser, UserUpdateRequest request) {
        if (!currentUser.getUsername().equals(request.username())) {
            if (userRepository.existsByUsername(request.username())) {
                throw new BusinessException("Username already exists");
            }
        }
        if (!currentUser.getEmail().equals(request.email().trim())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new BusinessException("Email already exists");
            }
        }
    }


}
