package dev.gamified.GamifiedPlatform.services.grimoire;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.response.grimoire.GrimoireResponse;
import dev.gamified.GamifiedPlatform.enums.Roles;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.repository.GrimoireDownloadRepository;
import dev.gamified.GamifiedPlatform.repository.GrimoireRepository;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UploadGrimoireServiceTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private GrimoireRepository grimoireRepository;

    @Mock
    private GrimoireDownloadRepository downloadRepository;

    @Mock
    private LevelRepository levelRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UploadGrimoireService uploadGrimoireService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ReflectionTestUtils.setField(
                uploadGrimoireService,
                "grimoiresBucket",
                "grimoires-test"
        );
    }

    @Test
    @DisplayName("Upload válido deve retornar resposta")
    void testUploadValido() throws Exception {

        Levels level = new Levels();
        level.setId(1L);
        level.setOrderLevel(1);
        level.setName("Beginner");

        PlayerCharacter pc = new PlayerCharacter();
        pc.setLevel(10);

        User admin = new User();
        admin.setId(1L);
        admin.setRole(Roles.ROLE_ADMIN);
        admin.setPlayerCharacter(pc);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "file.pdf",
                "application/pdf",
                new byte[]{1, 2, 3}
        );

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {

            securityMock.when(SecurityUtils::getCurrentUserId)
                    .thenReturn(Optional.of(1L));

            when(userRepository.findById(1L))
                    .thenReturn(Optional.of(admin));

            when(levelRepository.findById(1L))
                    .thenReturn(Optional.of(level));

            when(grimoireRepository.existsByLevelId(1L))
                    .thenReturn(false);

            when(grimoireRepository.save(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            when(downloadRepository.countByGrimoireId(any()))
                    .thenReturn(0L);

            when(downloadRepository.existsByGrimoireIdAndUserId(any(), any()))
                    .thenReturn(false);

            GrimoireResponse response =
                    uploadGrimoireService.execute(1L, file, "desc");

            assertNotNull(response);
            assertEquals("file.pdf", response.originalName());
            assertEquals("Beginner", response.levelName());
        }
    }

    @Test
    @DisplayName("Arquivo inválido deve lançar exceção")
    void testUploadArquivoInvalido() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "file.txt",
                "text/plain",
                new byte[]{1, 2, 3}
        );

        User admin = new User();
        admin.setId(1L);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {

            securityMock.when(SecurityUtils::getCurrentUserId)
                    .thenReturn(Optional.of(1L));

            when(userRepository.findById(1L))
                    .thenReturn(Optional.of(admin));

            assertThrows(
                    BusinessException.class,
                    () -> uploadGrimoireService.execute(1L, file, "desc")
            );
        }
    }


    @Test
    @DisplayName("Path traversal deve lançar exceção")
    void testPathTraversal() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "../../etc/passwd",
                "application/pdf",
                new byte[]{1, 2, 3}
        );

        User admin = new User();
        admin.setId(1L);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {

            securityMock.when(SecurityUtils::getCurrentUserId)
                    .thenReturn(Optional.of(1L));

            when(userRepository.findById(1L))
                    .thenReturn(Optional.of(admin));

            assertThrows(
                    BusinessException.class,
                    () -> uploadGrimoireService.execute(1L, file, "desc")
            );
        }
    }

    @Test
    @DisplayName("Exceção do MinIO deve ser convertida em BusinessException")
    void testMinioException() throws Exception {

        Levels level = new Levels();
        level.setId(1L);
        level.setOrderLevel(1);

        User admin = new User();
        admin.setId(1L);
        admin.setRole(Roles.ROLE_ADMIN);
        admin.setPlayerCharacter(new PlayerCharacter());

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "file.pdf",
                "application/pdf",
                new byte[]{1, 2, 3}
        );

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {

            securityMock.when(SecurityUtils::getCurrentUserId)
                    .thenReturn(Optional.of(1L));

            when(userRepository.findById(1L))
                    .thenReturn(Optional.of(admin));

            when(levelRepository.findById(1L))
                    .thenReturn(Optional.of(level));

            when(grimoireRepository.existsByLevelId(1L))
                    .thenReturn(false);

            doThrow(new RuntimeException("MinIO error"))
                    .when(minioClient).putObject(any());

            assertThrows(BusinessException.class,
                    () -> uploadGrimoireService.execute(1L, file, "desc"));
        }
    }
}
