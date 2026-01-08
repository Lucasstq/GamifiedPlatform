package dev.gamified.GamifiedPlatform.services.ranking;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.dtos.response.MyRankingResponse;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import dev.gamified.GamifiedPlatform.repository.PlayerCharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetMyRankingService {

    private final PlayerCharacterRepository playerCharacterRepository;
    private final LevelRepository levelRepository;

    public MyRankingResponse execute() {

        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        log.info("Fetching ranking position for user: {}", userId);

        PlayerCharacter character = playerCharacterRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Character not found for user: " + userId));

        Integer position = calculatePlayerPosition(character);
        long totalPlayers = playerCharacterRepository.count();

        Levels level = levelRepository.findTopByOrderLevelLessThanEqualOrderByOrderLevelDesc(character.getLevel())
                .orElse(null);

        double percentile = totalPlayers > 0 ? ((totalPlayers - position + 1.0) / totalPlayers) * 100.0 : 0.0;

        return MyRankingResponse.builder()
                .position(position)
                .totalPlayers(totalPlayers)
                .characterName(character.getName())
                .level(character.getLevel())
                .xp(character.getXp())
                .levelName(level != null ? level.getName() : "Unknown")
                .levelTitle(level != null ? level.getTitle() : "Unknown")
                .percentile(Math.round(percentile * 100.0) / 100.0)
                .build();
    }

    private Integer calculatePlayerPosition(PlayerCharacter character) {

        Sort sort = Sort.by(Sort.Direction.DESC, "level", "xp");
        List<PlayerCharacter> allCharacters = playerCharacterRepository.findAll(sort);

        int position = 1;
        for (PlayerCharacter c : allCharacters) {
            if (c.getId().equals(character.getId())) {
                break;
            }
            position++;
        }

        return position;
    }
}
