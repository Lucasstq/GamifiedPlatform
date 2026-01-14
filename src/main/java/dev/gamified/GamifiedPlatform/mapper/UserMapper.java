package dev.gamified.GamifiedPlatform.mapper;

import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.request.user.UserRequest;
import dev.gamified.GamifiedPlatform.dtos.response.user.PlayerCharacterResponse;
import dev.gamified.GamifiedPlatform.dtos.response.user.UserResponse;
import dev.gamified.GamifiedPlatform.dtos.response.user.UserSimpleResponse;
import dev.gamified.GamifiedPlatform.enums.AuthProvider;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public static User toEntity(UserRequest request) {
        return User.builder()
                .username(request.username())
                .email(request.email())
                .password(request.password())
                .avatarUrl(request.avatarUrl())
                .provider(AuthProvider.LOCAL)  // Define explicitamente como LOCAL
                .build();
    }

    public static UserSimpleResponse toSimpleResponse(User user) {
        return UserSimpleResponse.builder()
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .character(PlayerCharacterResponse.builder()
                        .name(user.getPlayerCharacter().getName())
                        .level(user.getPlayerCharacter().getLevel())
                        .xp(user.getPlayerCharacter().getXp())
                        .build())
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
