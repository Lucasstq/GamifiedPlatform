package dev.gamified.GamifiedPlatform.services.grimoire;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.Grimoire;
import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.dtos.response.GrimoireResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.GrimoireDownloadRepository;
import dev.gamified.GamifiedPlatform.repository.GrimoireRepository;
import dev.gamified.GamifiedPlatform.repository.PlayerCharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAllGrimoiresService {

    private final GrimoireRepository grimoireRepository;
    private final GrimoireDownloadRepository downloadRepository;
    private final PlayerCharacterRepository playerCharacterRepository;

    public List<GrimoireResponse> execute() {

        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        log.info("Listing all grimoires for user {}", userId);

        PlayerCharacter character = playerCharacterRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Character not found for user: " + userId));

        List<Grimoire> grimoires = grimoireRepository.findAllOrderedByLevel();

        return grimoires.stream()
                .map(g -> buildGrimoireResponse(g, character.getLevel(), userId))
                .toList();
    }

    private GrimoireResponse buildGrimoireResponse(Grimoire grimoire, Integer userLevel, Long userId) {
        Levels level = grimoire.getLevel();

        boolean isAccessible = userLevel >= level.getOrderLevel();
        Long downloadCount = downloadRepository.countByGrimoireId(grimoire.getId());
        boolean userDownloaded = downloadRepository.existsByGrimoireIdAndUserId(grimoire.getId(), userId);

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
