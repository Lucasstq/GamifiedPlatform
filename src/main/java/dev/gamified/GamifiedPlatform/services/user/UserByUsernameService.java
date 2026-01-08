package dev.gamified.GamifiedPlatform.services.user;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.response.user.UserResponse;
import dev.gamified.GamifiedPlatform.enums.Roles;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.UserMapper;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserByUsernameService {

    private final UserRepository userRepository;

    public UserResponse execute(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        isOwnerOrAdmin(user.getId());

        return UserMapper.toResponse(user);
    }

    // Verifica se o usuário autenticado tem permissão para ver este perfil
    // Permite acesso se for o próprio usuário, admin ou mentor
    private void isOwnerOrAdmin(Long userId) {
        if (!SecurityUtils.isResourceOwnerOrAdmin(userId) && !SecurityUtils.hasRole(Roles.ROLE_MENTOR)) {
            throw new BusinessException("You do not have permission to view this user");
        }
    }


}
