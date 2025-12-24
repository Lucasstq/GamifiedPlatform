package dev.gamified.GamifiedPlatform.services.user;

import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.response.UserResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourseNotFoundException;
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
                .orElseThrow(() -> new ResourseNotFoundException("User not found"));
        return UserMapper.toResponse(user);
    }


}
