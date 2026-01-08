package dev.gamified.GamifiedPlatform.dtos.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PublicUserProfileResponse(
        Long id,
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
