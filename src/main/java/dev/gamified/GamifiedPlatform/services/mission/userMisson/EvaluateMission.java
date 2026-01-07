package dev.gamified.GamifiedPlatform.services.mission.userMisson;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.domain.UserMission;
import dev.gamified.GamifiedPlatform.dtos.request.mission.MissionEvaluationRequest;
import dev.gamified.GamifiedPlatform.dtos.response.UserMissionResponse;
import dev.gamified.GamifiedPlatform.enums.MissionStatus;
import dev.gamified.GamifiedPlatform.enums.Roles;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.MissionMapper;
import dev.gamified.GamifiedPlatform.repository.*;
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

    @Transactional
    public UserMissionResponse execute(Long userMissionId, Long mentorId, MissionEvaluationRequest request) {
        log.info("Mentor {} evaluating mission {}", mentorId, userMissionId);

        isOwnerOrAdmin(mentorId);

        UserMission userMission = findAndValidateUserMission(userMissionId);
        User mentor = validateMentor(mentorId);

        updateMissionEvaluation(userMission, mentor, request);
        processMissionResult(userMission, request.getApproved());

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

    // Verifica se o usuário autenticado é o dono do recurso ou um admin
    private void isOwnerOrAdmin(Long userId) {
        if (!SecurityUtils.isResourceOwnerOrAdmin(userId)) {
            throw new AccessDeniedException("You do not have permission to update this user");
        }
    }

    private User validateMentor(Long mentorId) {
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found"));

        if (mentor.getRole() != Roles.ROLE_MENTOR) {
            throw new BusinessException("User is not authorized to evaluate missions");
        }

        return mentor;
    }

    private void updateMissionEvaluation(UserMission userMission, User mentor, MissionEvaluationRequest request) {
        userMission.setEvaluatedBy(mentor);
        userMission.setFeedback(request.getFeedback());
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

        log.info("Mission approved. {} XP awarded to the user. {}",
                userMission.getMission().getXpReward(), userMission.getUser().getId());
    }

    private void rejectMission(UserMission userMission) {
        userMission.setStatus(MissionStatus.FAILED);
        log.info("Mission failed for user {}", userMission.getUser().getId());
    }

    private void grantXpToCharacter(UserMission userMission) {
        PlayerCharacter character = playerCharacterRepository.findByUserId(userMission.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Character not found"));

        // Usa o service para adicionar XP e recalcular nível baseado na tabela tb_levels
        addXpToCharacterService.execute(character.getId(), userMission.getMission().getXpReward());
    }
}
