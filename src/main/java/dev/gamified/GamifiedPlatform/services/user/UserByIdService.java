package dev.gamified.GamifiedPlatform.services.user;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.response.UserResponse;
import dev.gamified.GamifiedPlatform.enums.Roles;
import dev.gamified.GamifiedPlatform.exceptions.AcessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.ResourseNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.UserMapper;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserByIdService {

    private final UserRepository userRepository;

    public UserResponse execute(Long userId) {

        isOwnerOrAdmin(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourseNotFoundException("User not found"));
        return UserMapper.toResponse(user);
    }

    // Verifica se o usuário autenticado tem permissão para ver este perfil
    // Permite acesso se for o próprio usuário, admin ou mentor
    private void isOwnerOrAdmin(Long userId) {
        if (!SecurityUtils.isResourceOwnerOrAdmin(userId) && !SecurityUtils.hasRole(Roles.ROLE_MENTOR)) {
            throw new AcessDeniedException("You do not have permission to view this user");
        }
    }


}
