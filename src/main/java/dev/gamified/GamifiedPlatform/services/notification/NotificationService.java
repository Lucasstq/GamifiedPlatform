package dev.gamified.GamifiedPlatform.services.notification;

import dev.gamified.GamifiedPlatform.domain.Notification;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.enums.NotificationType;
import dev.gamified.GamifiedPlatform.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void createMissionEvaluatedNotification(User user, String missionTitle, boolean approved, Long missionId) {
        String title = approved ? "Missao Aprovada" : "Missao Reprovada";
        String message = approved
            ? String.format("Parabens! Sua submissao da missao '%s' foi aprovada.", missionTitle)
            : String.format("Sua submissao da missao '%s' foi reprovada. Verifique o feedback e tente novamente.", missionTitle);

        createNotification(user, NotificationType.MISSION_EVALUATED, title, message, missionId);
        log.info("Mission evaluated notification created for user {} - mission: {}, approved: {}",
                 user.getId(), missionTitle, approved);
    }

    @Transactional
    public void createLevelUpNotification(User user, Integer newLevel, String levelName) {
        String title = String.format("Level Up! Nivel %d", newLevel);
        String message = String.format("Parabens! Voce alcancou o nivel %d: %s", newLevel, levelName);

        createNotification(user, NotificationType.LEVEL_UP, title, message, newLevel.longValue());
        log.info("Level up notification created for user {} - level: {}", user.getId(), newLevel);
    }

    @Transactional
    public void createBadgeUnlockedNotification(User user, String badgeName, String badgeDescription, Long badgeId) {
        String title = "Badge Desbloqueado";
        String message = String.format("Voce desbloqueou o badge '%s': %s", badgeName, badgeDescription);

        createNotification(user, NotificationType.BADGE_UNLOCKED, title, message, badgeId);
        log.info("Badge unlocked notification created for user {} - badge: {}", user.getId(), badgeName);
    }

    @Transactional
    public void createBossEvaluatedNotification(User user, String bossName, boolean approved, Long bossId) {
        String title = approved ? "Boss Derrotado" : "Boss Nao Derrotado";
        String message = approved
            ? String.format("Parabens! Voce derrotou o boss '%s'!", bossName)
            : String.format("Sua tentativa contra '%s' falhou. Verifique o feedback e tente novamente.", bossName);

        createNotification(user, NotificationType.BOSS_EVALUATED, title, message, bossId);
        log.info("Boss evaluated notification created for user {} - boss: {}, approved: {}",
                 user.getId(), bossName, approved);
    }

    @Transactional
    public void createBossUnlockedNotification(User user, String bossName, String levelName, Long bossId) {
        String title = "Novo Boss Desbloqueado";
        String message = String.format("Voce completou todas as missoes de '%s' e desbloqueou o boss: %s",
                                       levelName, bossName);

        createNotification(user, NotificationType.BOSS_UNLOCKED, title, message, bossId);
        log.info("Boss unlocked notification created for user {} - boss: {}", user.getId(), bossName);
    }

    @Transactional
    public void createGrimoireUnlockedNotification(User user, String levelName, Long levelId) {
        String title = "Grimorio Desbloqueado";
        String message = String.format("Voce alcancou o nivel '%s' e desbloqueou um novo grimorio!", levelName);

        createNotification(user, NotificationType.GRIMOIRE_UNLOCKED, title, message, levelId);
        log.info("Grimoire unlocked notification created for user {} - level: {}", user.getId(), levelName);
    }

    private void createNotification(User user, NotificationType type, String title, String message, Long referenceId) {
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .referenceId(referenceId)
                .build();

        notificationRepository.save(notification);
    }
}

