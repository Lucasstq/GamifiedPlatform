package dev.gamified.GamifiedPlatform.services.missions;

import dev.gamified.GamifiedPlatform.domain.*;
import dev.gamified.GamifiedPlatform.dtos.request.mission.MissionEvaluationRequest;
import dev.gamified.GamifiedPlatform.dtos.response.user.UserMissionResponse;
import dev.gamified.GamifiedPlatform.enums.MissionStatus;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.PlayerCharacterRepository;
import dev.gamified.GamifiedPlatform.repository.UserMissionRepository;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import dev.gamified.GamifiedPlatform.services.mission.userMission.EvaluateMission;
import dev.gamified.GamifiedPlatform.services.notification.NotificationService;
import dev.gamified.GamifiedPlatform.services.playerCharacter.AddXpToCharacterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EvaluateMission Service Tests")
class EvaluateMissionTest {

    @Mock
    private UserMissionRepository userMissionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlayerCharacterRepository playerCharacterRepository;

    @Mock
    private AddXpToCharacterService addXpToCharacterService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private EvaluateMission evaluateMission;

    private User mentor;
    private User student;
    private PlayerCharacter character;
    private Mission mission;
    private UserMission userMission;
    private MissionEvaluationRequest approvalRequest;
    private MissionEvaluationRequest rejectionRequest;

    @BeforeEach
    void setUp() {
        // Setup mentor
        mentor = User.builder()
                .id(1L)
                .username("mentor")
                .email("mentor@test.com")
                .build();

        // Setup student
        student = User.builder()
                .id(2L)
                .username("student")
                .email("student@test.com")
                .build();

        // Setup character
        character = PlayerCharacter.builder()
                .id(1L)
                .user(student)
                .level(1)
                .xp(100)
                .build();

        // Setup level
        Levels level = Levels.builder()
                .id(1L)
                .orderLevel(1)
                .name("Iniciante")
                .xpRequired(0)
                .build();

        // Setup mission
        mission = Mission.builder()
                .id(1L)
                .level(level)
                .title("Primeira Missão")
                .description("Descrição da missão")
                .xpReward(50)
                .orderNumber(1)
                .build();

        // Setup user mission
        userMission = UserMission.builder()
                .id(1L)
                .user(student)
                .mission(mission)
                .status(MissionStatus.AWAITING_EVALUATION)
                .submissionUrl("https://github.com/user/repo")
                .build();

        // Setup evaluation requests
        approvalRequest = MissionEvaluationRequest.builder()
                .approved(true)
                .feedback("Excelente trabalho!")
                .build();

        rejectionRequest = MissionEvaluationRequest.builder()
                .approved(false)
                .feedback("Precisa melhorar a implementação")
                .build();
    }

    @Test
    @DisplayName("Deve aprovar missão com sucesso")
    void shouldApproveMissionSuccessfully() {
        // Arrange
        try (MockedStatic<dev.gamified.GamifiedPlatform.config.security.SecurityUtils> mockedSecurityUtils =
                     mockStatic(dev.gamified.GamifiedPlatform.config.security.SecurityUtils.class)) {

            mockedSecurityUtils.when(dev.gamified.GamifiedPlatform.config.security.SecurityUtils::getCurrentUserId)
                    .thenReturn(Optional.of(mentor.getId()));

            when(userRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
            when(userMissionRepository.findById(userMission.getId())).thenReturn(Optional.of(userMission));
            when(playerCharacterRepository.findByUserId(student.getId())).thenReturn(Optional.of(character));
            when(userMissionRepository.save(any(UserMission.class))).thenReturn(userMission);

            // Act
            UserMissionResponse response = evaluateMission.execute(userMission.getId(), approvalRequest);

            // Assert
            assertNotNull(response);
            assertEquals(MissionStatus.COMPLETED, userMission.getStatus());
            assertEquals(mentor, userMission.getEvaluatedBy());
            assertEquals(approvalRequest.feedback(), userMission.getFeedback());
            assertNotNull(userMission.getEvaluatedAt());
            assertNotNull(userMission.getCompletedAt());

            verify(userMissionRepository).save(userMission);
            verify(addXpToCharacterService).execute(character.getId(), mission.getXpReward());
            verify(notificationService).createMissionEvaluatedNotification(
                    eq(student),
                    eq(mission.getTitle()),
                    eq(true),
                    eq(mission.getId())
            );
        }
    }

    @Test
    @DisplayName("Deve rejeitar a missão com sucesso")
    void shouldRejectMissionSuccessfully() {
        // Arrange
        try (MockedStatic<dev.gamified.GamifiedPlatform.config.security.SecurityUtils> mockedSecurityUtils =
                     mockStatic(dev.gamified.GamifiedPlatform.config.security.SecurityUtils.class)) {

            mockedSecurityUtils.when(dev.gamified.GamifiedPlatform.config.security.SecurityUtils::getCurrentUserId)
                    .thenReturn(Optional.of(mentor.getId()));

            when(userRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
            when(userMissionRepository.findById(userMission.getId())).thenReturn(Optional.of(userMission));
            when(userMissionRepository.save(any(UserMission.class))).thenReturn(userMission);

            // Act
            UserMissionResponse response = evaluateMission.execute(userMission.getId(), rejectionRequest);

            // Assert
            assertNotNull(response);
            assertEquals(MissionStatus.FAILED, userMission.getStatus());
            assertEquals(mentor, userMission.getEvaluatedBy());
            assertEquals(rejectionRequest.feedback(), userMission.getFeedback());
            assertNotNull(userMission.getEvaluatedAt());
            assertNull(userMission.getCompletedAt());

            verify(userMissionRepository).save(userMission);
            verify(addXpToCharacterService, never()).execute(anyLong(), anyInt());
            verify(notificationService).createMissionEvaluatedNotification(
                    eq(student),
                    eq(mission.getTitle()),
                    eq(false),
                    eq(mission.getId())
            );
        }
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando usuário não autenticado")
    void shouldThrowAccessDeniedExceptionWhenUserNotAuthenticated() {
        // Arrange
        try (MockedStatic<dev.gamified.GamifiedPlatform.config.security.SecurityUtils> mockedSecurityUtils =
                     mockStatic(dev.gamified.GamifiedPlatform.config.security.SecurityUtils.class)) {

            mockedSecurityUtils.when(dev.gamified.GamifiedPlatform.config.security.SecurityUtils::getCurrentUserId)
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(AccessDeniedException.class,
                    () -> evaluateMission.execute(userMission.getId(), approvalRequest));

            verify(userMissionRepository, never()).findById(anyLong());
            verify(userMissionRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando missão do usuário não for encontrada")
    void shouldThrowResourceNotFoundExceptionWhenUserMissionNotFound() {
        // Arrange
        try (MockedStatic<dev.gamified.GamifiedPlatform.config.security.SecurityUtils> mockedSecurityUtils =
                     mockStatic(dev.gamified.GamifiedPlatform.config.security.SecurityUtils.class)) {

            mockedSecurityUtils.when(dev.gamified.GamifiedPlatform.config.security.SecurityUtils::getCurrentUserId)
                    .thenReturn(Optional.of(mentor.getId()));

            when(userRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
            when(userMissionRepository.findById(userMission.getId())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class,
                    () -> evaluateMission.execute(userMission.getId(), approvalRequest));

            verify(userMissionRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando missão não estiver aguardando avaliação")
    void shouldThrowBusinessExceptionWhenMissionNotAwaitingEvaluation() {
        // Arrange
        userMission.setStatus(MissionStatus.IN_PROGRESS);

        try (MockedStatic<dev.gamified.GamifiedPlatform.config.security.SecurityUtils> mockedSecurityUtils =
                     mockStatic(dev.gamified.GamifiedPlatform.config.security.SecurityUtils.class)) {

            mockedSecurityUtils.when(dev.gamified.GamifiedPlatform.config.security.SecurityUtils::getCurrentUserId)
                    .thenReturn(Optional.of(mentor.getId()));

            when(userRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
            when(userMissionRepository.findById(userMission.getId())).thenReturn(Optional.of(userMission));

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> evaluateMission.execute(userMission.getId(), approvalRequest));

            assertTrue(exception.getMessage().contains("Only questions that are pending evaluation can be assessed"));
            verify(userMissionRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando personagem do usuário não for encontrado")
    void shouldThrowResourceNotFoundExceptionWhenCharacterNotFound() {
        // Arrange
        try (MockedStatic<dev.gamified.GamifiedPlatform.config.security.SecurityUtils> mockedSecurityUtils =
                     mockStatic(dev.gamified.GamifiedPlatform.config.security.SecurityUtils.class)) {

            mockedSecurityUtils.when(dev.gamified.GamifiedPlatform.config.security.SecurityUtils::getCurrentUserId)
                    .thenReturn(Optional.of(mentor.getId()));

            when(userRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
            when(userMissionRepository.findById(userMission.getId())).thenReturn(Optional.of(userMission));
            when(playerCharacterRepository.findByUserId(student.getId())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class,
                    () -> evaluateMission.execute(userMission.getId(), approvalRequest));

            verify(userMissionRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("Deve definir os metadados de avaliação corretamente ao aprovar")
    void shouldSetEvaluationMetadataCorrectlyWhenApproving() {
        // Arrange
        try (MockedStatic<dev.gamified.GamifiedPlatform.config.security.SecurityUtils> mockedSecurityUtils =
                     mockStatic(dev.gamified.GamifiedPlatform.config.security.SecurityUtils.class)) {

            mockedSecurityUtils.when(dev.gamified.GamifiedPlatform.config.security.SecurityUtils::getCurrentUserId)
                    .thenReturn(Optional.of(mentor.getId()));

            when(userRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
            when(userMissionRepository.findById(userMission.getId())).thenReturn(Optional.of(userMission));
            when(playerCharacterRepository.findByUserId(student.getId())).thenReturn(Optional.of(character));
            when(userMissionRepository.save(any(UserMission.class))).thenReturn(userMission);

            // Act
            evaluateMission.execute(userMission.getId(), approvalRequest);

            // Assert
            assertEquals(mentor, userMission.getEvaluatedBy());
            assertEquals(approvalRequest.feedback(), userMission.getFeedback());
            assertNotNull(userMission.getEvaluatedAt());
            assertNotNull(userMission.getCompletedAt());
            assertEquals(MissionStatus.COMPLETED, userMission.getStatus());
        }
    }

    @Test
    @DisplayName("Deve definir os metadados de avaliação corretamente ao rejeitar")
    void shouldSetEvaluationMetadataCorrectlyWhenRejecting() {
        // Arrange
        try (MockedStatic<dev.gamified.GamifiedPlatform.config.security.SecurityUtils> mockedSecurityUtils =
                     mockStatic(dev.gamified.GamifiedPlatform.config.security.SecurityUtils.class)) {

            mockedSecurityUtils.when(dev.gamified.GamifiedPlatform.config.security.SecurityUtils::getCurrentUserId)
                    .thenReturn(Optional.of(mentor.getId()));

            when(userRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
            when(userMissionRepository.findById(userMission.getId())).thenReturn(Optional.of(userMission));
            when(userMissionRepository.save(any(UserMission.class))).thenReturn(userMission);

            // Act
            evaluateMission.execute(userMission.getId(), rejectionRequest);

            // Assert
            assertEquals(mentor, userMission.getEvaluatedBy());
            assertEquals(rejectionRequest.feedback(), userMission.getFeedback());
            assertNotNull(userMission.getEvaluatedAt());
            assertNull(userMission.getCompletedAt());
            assertEquals(MissionStatus.FAILED, userMission.getStatus());
        }
    }

    @Test
    @DisplayName("Deve-se chamar o serviço AddXpToCharacterService com os parâmetros corretos ao aprovar.")
    void shouldCallAddXpToCharacterServiceWithCorrectParametersWhenApproving() {
        // Arrange
        try (MockedStatic<dev.gamified.GamifiedPlatform.config.security.SecurityUtils> mockedSecurityUtils =
                     mockStatic(dev.gamified.GamifiedPlatform.config.security.SecurityUtils.class)) {

            mockedSecurityUtils.when(dev.gamified.GamifiedPlatform.config.security.SecurityUtils::getCurrentUserId)
                    .thenReturn(Optional.of(mentor.getId()));

            when(userRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
            when(userMissionRepository.findById(userMission.getId())).thenReturn(Optional.of(userMission));
            when(playerCharacterRepository.findByUserId(student.getId())).thenReturn(Optional.of(character));
            when(userMissionRepository.save(any(UserMission.class))).thenReturn(userMission);

            // Act
            evaluateMission.execute(userMission.getId(), approvalRequest);

            // Assert
            verify(addXpToCharacterService).execute(character.getId(), mission.getXpReward());
        }
    }

    @Test
    @DisplayName("Deve enviar notificação correta quando missão for aprovada")
    void shouldSendCorrectNotificationWhenMissionIsApproved() {
        // Arrange
        try (MockedStatic<dev.gamified.GamifiedPlatform.config.security.SecurityUtils> mockedSecurityUtils =
                     mockStatic(dev.gamified.GamifiedPlatform.config.security.SecurityUtils.class)) {

            mockedSecurityUtils.when(dev.gamified.GamifiedPlatform.config.security.SecurityUtils::getCurrentUserId)
                    .thenReturn(Optional.of(mentor.getId()));

            when(userRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
            when(userMissionRepository.findById(userMission.getId())).thenReturn(Optional.of(userMission));
            when(playerCharacterRepository.findByUserId(student.getId())).thenReturn(Optional.of(character));
            when(userMissionRepository.save(any(UserMission.class))).thenReturn(userMission);

            // Act
            evaluateMission.execute(userMission.getId(), approvalRequest);

            // Assert
            verify(notificationService).createMissionEvaluatedNotification(
                    student,
                    mission.getTitle(),
                    true,
                    mission.getId()
            );
        }
    }

    @Test
    @DisplayName("Deve enviar notificação correta quando missão for rejeitada")
    void shouldSendCorrectNotificationWhenMissionIsRejected() {
        // Arrange
        try (MockedStatic<dev.gamified.GamifiedPlatform.config.security.SecurityUtils> mockedSecurityUtils =
                     mockStatic(dev.gamified.GamifiedPlatform.config.security.SecurityUtils.class)) {

            mockedSecurityUtils.when(dev.gamified.GamifiedPlatform.config.security.SecurityUtils::getCurrentUserId)
                    .thenReturn(Optional.of(mentor.getId()));

            when(userRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
            when(userMissionRepository.findById(userMission.getId())).thenReturn(Optional.of(userMission));
            when(userMissionRepository.save(any(UserMission.class))).thenReturn(userMission);

            // Act
            evaluateMission.execute(userMission.getId(), rejectionRequest);

            // Assert
            verify(notificationService).createMissionEvaluatedNotification(
                    student,
                    mission.getTitle(),
                    false,
                    mission.getId()
            );
        }
    }
}
