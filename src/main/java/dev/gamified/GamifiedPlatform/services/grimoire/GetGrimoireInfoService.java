package dev.gamified.GamifiedPlatform.services.grimoire;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.Grimoire;
import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.response.grimoire.GrimoireResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.GrimoireDownloadRepository;
import dev.gamified.GamifiedPlatform.repository.GrimoireRepository;
import dev.gamified.GamifiedPlatform.repository.PlayerCharacterRepository;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetGrimoireInfoService {

    private final GrimoireRepository grimoireRepository;
    private final GrimoireDownloadRepository downloadRepository;
    private final PlayerCharacterRepository playerCharacterRepository;
    private final UserRepository userRepository;

    public GrimoireResponse execute(Long levelId) {

        User currentUser = SecurityUtils.getCurrentUserId()
                .flatMap(userRepository::findById)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        log.info("Getting grimoire info for level {} by user {}", levelId, currentUser.getId());

        Grimoire grimoire = grimoireRepository.findByLevelId(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Grimoire not found for level: " + levelId));

        PlayerCharacter character = playerCharacterRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Character not found for user: " + currentUser.getId()));

        Levels level = grimoire.getLevel();
        boolean isAccessible = character.getLevel() >= level.getOrderLevel();

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
