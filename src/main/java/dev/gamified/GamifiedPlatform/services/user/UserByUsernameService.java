package dev.gamified.GamifiedPlatform.services.user;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.response.user.UserResponse;
import dev.gamified.GamifiedPlatform.dtos.response.user.UserSimpleResponse;
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

    public UserSimpleResponse execute(String username) {

        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserMapper.toSimpleResponse(user);
    }


}
