package dev.gamified.GamifiedPlatform.mapper;

import dev.gamified.GamifiedPlatform.domain.Badge;
import dev.gamified.GamifiedPlatform.domain.Boss;
import dev.gamified.GamifiedPlatform.domain.UserBadge;
import dev.gamified.GamifiedPlatform.dtos.response.BadgeResponse;
import dev.gamified.GamifiedPlatform.dtos.response.UserBadgeResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BadgeMapper {

    public static BadgeResponse toResponse(Badge badge) {
        return BadgeResponse.builder()
                .id(badge.getId())
                .levelId(badge.getLevel().getId())
                .levelName(badge.getLevel().getName())
                .levelOrder(badge.getLevel().getOrderLevel())
                .name(badge.getName())
                .title(badge.getTitle())
                .description(badge.getDescription())
                .iconUrl(badge.getIconUrl())
                .rarity(badge.getRarity())
                .createdAt(badge.getCreatedAt())
                .build();
    }

    public static UserBadgeResponse toUserBadgeResponse(UserBadge userBadge) {
        return UserBadgeResponse.builder()
                .id(userBadge.getId())
                .userId(userBadge.getUser().getId())
                .username(userBadge.getUser().getUsername())
                .badge(toResponse(userBadge.getBadge()))
                .unlockedAt(userBadge.getUnlockedAt())
                .unlockedByBossId(userBadge.getUnlockedByBossId())
                .build();
    }

    public static UserBadgeResponse toUserBadgeResponseWithBoss(UserBadge userBadge, Boss boss) {
        return UserBadgeResponse.builder()
                .id(userBadge.getId())
                .userId(userBadge.getUser().getId())
                .username(userBadge.getUser().getUsername())
                .badge(toResponse(userBadge.getBadge()))
                .unlockedAt(userBadge.getUnlockedAt())
                .unlockedByBossId(userBadge.getUnlockedByBossId())
                .unlockedByBossName(boss != null ? boss.getName() : null)
                .build();
    }
}

