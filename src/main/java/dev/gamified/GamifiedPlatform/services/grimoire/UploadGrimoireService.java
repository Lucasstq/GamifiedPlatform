package dev.gamified.GamifiedPlatform.services.grimoire;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.Grimoire;
import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.response.GrimoireResponse;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.GrimoireDownloadRepository;
import dev.gamified.GamifiedPlatform.repository.GrimoireRepository;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadGrimoireService {

    private static final String ALLOWED_CONTENT_TYPE = "application/pdf";
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;
    private final MinioClient minioClient;
    private final GrimoireRepository grimoireRepository;
    private final GrimoireDownloadRepository downloadRepository;
    private final LevelRepository levelRepository;
    private final UserRepository userRepository;

    @Value("${minio.bucket.grimoires}")
    private String grimoiresBucket;

    @Transactional
    public GrimoireResponse execute(Long levelId, MultipartFile file, String description) {
        log.info("Uploading grimoire for level {} by admin user ", levelId);

        User admin = SecurityUtils.getCurrentUserId()
                .flatMap(userRepository::findById)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

        validateFile(file);
        Levels level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found with id: " + levelId));

        if (grimoireRepository.existsByLevelId(levelId)) {
            throw new BusinessException("A grimoire already exists for level: " + level.getName());
        }

        try {
            String originalFilename = file.getOriginalFilename();

            String fileExtension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".pdf";

            String uniqueFileName = UUID.randomUUID() + fileExtension;
            String objectKey = "level-" + level.getOrderLevel() + "/" + uniqueFileName;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(grimoiresBucket)
                            .object(objectKey)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            Grimoire grimoire = Grimoire.builder()
                    .level(level)
                    .fileName(uniqueFileName)
                    .originalName(originalFilename)
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .minioBucket(grimoiresBucket)
                    .minioObjectKey(objectKey)
                    .description(description)
                    .uploadedAt(LocalDateTime.now())
                    .uploadedBy(admin)
                    .build();
            grimoire = grimoireRepository.save(grimoire);
            log.info("Grimoire uploaded successfully - id: {}, level: {}", grimoire.getId(), level.getName());
            return buildGrimoireResponse(grimoire);
        } catch (Exception e) {
            log.error("Error uploading grimoire to MinIO", e);
            throw new BusinessException("Failed to upload grimoire: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("File cannot be empty");
        }

        String contentType = file.getContentType();

        if (!ALLOWED_CONTENT_TYPE.equals(contentType)) {
            throw new BusinessException("Only PDF files are allowed. Received: " + contentType);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(
                    String.format("File size exceeds maximum allowed (50 MB). File size: %.2f MB",
                            file.getSize() / (1024.0 * 1024.0))
            );
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new BusinessException("File must have .pdf extension");
        }

        // 🔒 SEGURANÇA: Validar path traversal e caracteres perigosos no nome do arquivo
        if (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
            throw new BusinessException("Invalid filename. Path traversal detected.");
        }

        // 🔒 SEGURANÇA: Validar caracteres maliciosos
        if (!originalFilename.matches("^[a-zA-Z0-9._\\-\\s]+\\.pdf$")) {
            throw new BusinessException("Filename contains invalid characters. Only alphanumeric, spaces, dots, hyphens and underscores are allowed.");
        }
    }

    private GrimoireResponse buildGrimoireResponse(Grimoire grimoire) {

        Levels level = grimoire.getLevel();

        User currentUser = SecurityUtils.getCurrentUserId()
                .flatMap(userRepository::findById)
                .orElseThrow(() -> new ResourceNotFoundException("User user not found"));

        boolean isAccessible = currentUser.getPlayerCharacter().getLevel() >= level.getOrderLevel();
        Long downloadCount = downloadRepository.countByGrimoireId(grimoire.getId());
        boolean userDownloaded = downloadRepository.existsByGrimoireIdAndUserId(grimoire.getId(), currentUser.getId());

        return GrimoireResponse.builder()
                .id(grimoire.getId())
                .levelId(level.getId())
                .levelName(level.getName())
                .levelOrder(level.getOrderLevel())
                .fileName(grimoire.getFileName())
                .originalName(grimoire.getOriginalName())
                .fileSize(grimoire.getFileSize())
                .description(grimoire.getDescription())
                .uploadedAt(grimoire.getUploadedAt())
                .isAccessible(isAccessible)
                .downloadCount(downloadCount)
                .userDownloaded(userDownloaded)
                .build();
    }

}
