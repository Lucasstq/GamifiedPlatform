package dev.gamified.GamifiedPlatform.services.user;

import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.response.user.UserSimpleResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.UserMapper;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserByIdService {

    private final UserRepository userRepository;

    public UserSimpleResponse execute(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserMapper.toSimpleResponse(user);
    }


}
