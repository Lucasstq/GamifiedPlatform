package dev.gamified.GamifiedPlatform.mapper;

import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.request.user.UserRequest;
import dev.gamified.GamifiedPlatform.dtos.response.user.UserResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public static User toEntity(UserRequest request) {
        return User.builder()
                .username(request.username())
                .email(request.email())
                .password(request.password())
                .avatarUrl(request.avatarUrl())
                .build();
    }

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }


}
