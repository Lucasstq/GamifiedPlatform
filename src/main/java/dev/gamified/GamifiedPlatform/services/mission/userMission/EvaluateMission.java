package dev.gamified.GamifiedPlatform.services.mission.userMission;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.domain.UserMission;
import dev.gamified.GamifiedPlatform.dtos.request.mission.MissionEvaluationRequest;
import dev.gamified.GamifiedPlatform.dtos.response.user.UserMissionResponse;
import dev.gamified.GamifiedPlatform.enums.MissionStatus;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.MissionMapper;
import dev.gamified.GamifiedPlatform.repository.PlayerCharacterRepository;
import dev.gamified.GamifiedPlatform.repository.UserMissionRepository;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import dev.gamified.GamifiedPlatform.services.notification.NotificationService;
import dev.gamified.GamifiedPlatform.services.playerCharacter.AddXpToCharacterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvaluateMission {

    private final UserMissionRepository userMissionRepository;
    private final UserRepository userRepository;
    private final PlayerCharacterRepository playerCharacterRepository;
    private final AddXpToCharacterService addXpToCharacterService;
    private final NotificationService notificationService;

    @Transactional
    public UserMissionResponse execute(Long userMissionId, MissionEvaluationRequest request) {

        User currentUser = SecurityUtils.getCurrentUserId()
                .flatMap(userRepository::findById)
                .orElseThrow(() -> new AccessDeniedException("User not authenticated"));

        log.info("Mentor {} evaluating mission {}", currentUser.getId(), userMissionId);

        UserMission userMission = findAndValidateUserMission(userMissionId);

        updateMissionEvaluation(userMission, currentUser, request);
        processMissionResult(userMission, request.approved());

        UserMission savedUserMission = userMissionRepository.save(userMission);
        return MissionMapper.toUserMissionResponse(savedUserMission);
    }

    private UserMission findAndValidateUserMission(Long userMissionId) {
        UserMission userMission = userMissionRepository.findById(userMissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission submission not found"));

        if (userMission.getStatus() != MissionStatus.AWAITING_EVALUATION) {
            throw new BusinessException("Only questions that are pending evaluation can be assessed");
        }

        return userMission;
    }

    private void updateMissionEvaluation(UserMission userMission, User mentor, MissionEvaluationRequest request) {
        userMission.setEvaluatedBy(mentor);
        userMission.setFeedback(request.feedback());
        userMission.setEvaluatedAt(LocalDateTime.now());
    }

    private void processMissionResult(UserMission userMission, Boolean approved) {
        if (approved) {
            approveMission(userMission);
        } else {
            rejectMission(userMission);
        }
    }

    private void approveMission(UserMission userMission) {
        userMission.setStatus(MissionStatus.COMPLETED);
        userMission.setCompletedAt(LocalDateTime.now());

        grantXpToCharacter(userMission);

        notificationService.createMissionEvaluatedNotification(
                userMission.getUser(),
                userMission.getMission().getTitle(),
                true,
                userMission.getMission().getId()
        );

        log.info("Mission approved. {} XP awarded to the user. {}",
                userMission.getMission().getXpReward(), userMission.getUser().getId());
    }

    private void rejectMission(UserMission userMission) {
        userMission.setStatus(MissionStatus.FAILED);

        notificationService.createMissionEvaluatedNotification(
                userMission.getUser(),
                userMission.getMission().getTitle(),
                false,
                userMission.getMission().getId()
        );

        log.info("Mission failed for user {}", userMission.getUser().getId());
    }

    private void grantXpToCharacter(UserMission userMission) {
        PlayerCharacter character = playerCharacterRepository.findByUserId(userMission.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Character not found"));

        // Usa o service para adicionar XP e recalcular nível baseado na tabela tb_levels
        addXpToCharacterService.execute(character.getId(), userMission.getMission().getXpReward());
    }
}
