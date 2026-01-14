package dev.gamified.GamifiedPlatform.dtos.response.user;

import dev.gamified.GamifiedPlatform.dtos.response.badges.BadgeProgressResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PublicUserProfileResponse(
        String username,
        String avatarUrl,
        LocalDateTime createdAt,
        String characterName,
        Integer level,
        Integer xp,
        List<UserBadgeResponse> badges,
        BadgeProgressResponse badgeProgress
) {
}
