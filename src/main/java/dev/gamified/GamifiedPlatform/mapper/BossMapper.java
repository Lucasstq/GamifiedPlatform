package dev.gamified.GamifiedPlatform.mapper;

import dev.gamified.GamifiedPlatform.domain.Boss;
import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.domain.UserBoss;
import dev.gamified.GamifiedPlatform.dtos.request.boss.BossCreateRequest;
import dev.gamified.GamifiedPlatform.dtos.request.boss.BossFightEvaluationRequest;
import dev.gamified.GamifiedPlatform.dtos.request.boss.BossFightSubmissionRequest;
import dev.gamified.GamifiedPlatform.dtos.response.BossResponse;
import dev.gamified.GamifiedPlatform.dtos.response.UserBossResponse;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class BossMapper {

    /*
     * Converte um BossCreateRequest e Level para uma entidade Boss.
     */
    public static Boss toEntity(BossCreateRequest request, Levels level) {
        return Boss.builder()
                .level(level)
                .name(request.name())
                .title(request.title())
                .description(request.description())
                .challenge(request.challenge())
                .xpReward(request.xpReward())
                .badgeName(request.badgeName())
                .badgeDescription(request.badgeDescription())
                .imageUrl(request.imageUrl())
                .badgeIconUrl(request.badgeIconUrl())
                .unlocksNextLevel(request.unlocksNextLevel() != null ? request.unlocksNextLevel() : true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static BossResponse toResponse(Boss boss) {
        return BossResponse.builder()
                .id(boss.getId())
                .levelId(boss.getLevel().getId())
                .levelName(boss.getLevel().getName())
                .name(boss.getName())
                .title(boss.getTitle())
                .description(boss.getDescription())
                .challenge(boss.getChallenge())
                .xpReward(boss.getXpReward())
                .badgeName(boss.getBadgeName())
                .badgeDescription(boss.getBadgeDescription())
                .imageUrl(boss.getImageUrl())
                .badgeIconUrl(boss.getBadgeIconUrl())
                .unlocksNextLevel(boss.getUnlocksNextLevel())
                .build();
    }

    public static UserBossResponse toUserBossResponse(UserBoss userBoss) {
        return UserBossResponse.builder()
                .id(userBoss.getId())
                .bossId(userBoss.getBoss().getId())
                .bossName(userBoss.getBoss().getName())
                .bossTitle(userBoss.getBoss().getTitle())
                .bossDescription(userBoss.getBoss().getDescription())
                .bossChallenge(userBoss.getBoss().getChallenge())
                .xpReward(userBoss.getBoss().getXpReward())
                .badgeName(userBoss.getBoss().getBadgeName())
                .badgeDescription(userBoss.getBoss().getBadgeDescription())
                .imageUrl(userBoss.getBoss().getImageUrl())
                .badgeIconUrl(userBoss.getBoss().getBadgeIconUrl())
                .status(userBoss.getStatus())
                .submissionUrl(userBoss.getSubmissionUrl())
                .submissionNotes(userBoss.getSubmissionNotes())
                .feedback(userBoss.getFeedback())
                .evaluatedByName(userBoss.getEvaluatedBy() != null ? userBoss.getEvaluatedBy().getUsername() : null)
                .startedAt(userBoss.getStartedAt())
                .submittedAt(userBoss.getSubmittedAt())
                .evaluatedAt(userBoss.getEvaluatedAt())
                .completedAt(userBoss.getCompletedAt())
                .unlockedAt(userBoss.getUnlockedAt())
                .build();
    }

    /*
     * Aplica os dados de submissão do request para a entidade UserBoss.
     */
    public static void applySubmissionRequest(UserBoss userBoss, BossFightSubmissionRequest request) {
        userBoss.setSubmissionUrl(request.submissionUrl());
        userBoss.setSubmissionNotes(request.submissionNotes());
    }

    public static void applyEvaluationRequest(UserBoss userBoss, BossFightEvaluationRequest request) {
        userBoss.setFeedback(request.feedback());
    }
}

