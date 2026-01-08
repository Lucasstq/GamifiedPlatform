package dev.gamified.GamifiedPlatform.services.grimoire;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.Grimoire;
import dev.gamified.GamifiedPlatform.domain.GrimoireDownload;
import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.GrimoireDownloadRepository;
import dev.gamified.GamifiedPlatform.repository.GrimoireRepository;
import dev.gamified.GamifiedPlatform.repository.PlayerCharacterRepository;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DownloadGrimoireService {

    private final MinioClient minioClient;
    private final GrimoireRepository grimoireRepository;
    private final GrimoireDownloadRepository downloadRepository;
    private final PlayerCharacterRepository playerCharacterRepository;
    private final UserRepository userRepository;

    @Transactional
    public InputStream execute(Long levelId) {

        User currentUser = SecurityUtils.getCurrentUserId()
                .flatMap(userRepository::findById)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        log.info("User{} attempting to download grimoire for level{}", currentUser.getId(), levelId);

        Grimoire grimoire = grimoireRepository.findByLevelId(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Grimoire not found for level: " + levelId));

        PlayerCharacter character = playerCharacterRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Character not found for user: " + currentUser.getId()));

        Integer requiredLevel = grimoire.getLevel().getOrderLevel();

        if (character.getLevel() < requiredLevel) {
            throw new AccessDeniedException(
                    String.format("You need to be at least level %d to access this grimoire. Your current level: %d",
                            requiredLevel, character.getLevel())
            );
        }

        try {
            User user = userRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found for ID " + currentUser.getId()));

            GrimoireDownload download = GrimoireDownload.builder()
                    .grimoire(grimoire)
                    .user(user)
                    .downloadedAt(LocalDateTime.now())
                    .userLevelAtDownload(character.getLevel())
                    .build();

            downloadRepository.save(download);
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(grimoire.getMinioBucket())
                            .object(grimoire.getMinioObjectKey())
                            .build()
            );

            log.info("Grimoire downloaded successfully - grimoire: {}, user: {}", grimoire.getId(), currentUser.getId());
            return stream;
        } catch (Exception e) {
            log.error("Error downloading grimoire from MinIO", e);
            throw new BusinessException("Failed to download grimoire: " + e.getMessage());
        }
    }

}
