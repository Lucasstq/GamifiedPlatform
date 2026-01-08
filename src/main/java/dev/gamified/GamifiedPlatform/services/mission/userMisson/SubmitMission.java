package dev.gamified.GamifiedPlatform.services.mission.userMisson;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.UserMission;
import dev.gamified.GamifiedPlatform.dtos.request.mission.MissionSubmissionRequest;
import dev.gamified.GamifiedPlatform.dtos.response.UserMissionResponse;
import dev.gamified.GamifiedPlatform.enums.MissionStatus;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.MissionMapper;
import dev.gamified.GamifiedPlatform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmitMission {

    private final UserMissionRepository userMissionRepository;

    @Transactional
    public UserMissionResponse execute(Long missionId, MissionSubmissionRequest request) {

        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new ResourceNotFoundException("User not authenticated"));

        log.info("User {} submitting mission {}", userId, missionId);

        validateUserPermission(userId);

        UserMission userMission = userMissionRepository.findByUserIdAndMissionId(userId, missionId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not started by user."));

        userCanSubmitMission(userMission);

        userMission.setStatus(MissionStatus.AWAITING_EVALUATION);
        userMission.setSubmissionUrl(request.submissionUrl());
        userMission.setSubmissionNotes(request.submissionNotes());
        userMission.setSubmittedAt(LocalDateTime.now());

        UserMission savedUserMission = userMissionRepository.save(userMission);
        log.info("Mission {} successfully submitted by user {}", missionId, userId);

        return MissionMapper.toUserMissionResponse(savedUserMission);
    }

    private void validateUserPermission(Long userId) {
        if (!SecurityUtils.isResourceOwnerOrAdmin(userId)) {
            throw new BusinessException("User not authorized to submit this mission.");
        }
    }

    private void userCanSubmitMission(UserMission userMission) {
        if (userMission.getStatus() != MissionStatus.IN_PROGRESS) {
            throw new BusinessException("Only missions in progress can be submitted.");
        }
    }
}
