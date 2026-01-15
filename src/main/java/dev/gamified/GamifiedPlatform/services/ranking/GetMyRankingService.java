package dev.gamified.GamifiedPlatform.services.ranking;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.constants.BusinessConstants;
import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.dtos.response.ranking.MyRankingResponse;
import dev.gamified.GamifiedPlatform.dtos.response.ranking.RankingInfo;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import dev.gamified.GamifiedPlatform.repository.PlayerCharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

        RankingInfo rankingInfo = playerCharacterRepository.findPlayerPosition(character.getId());

        Levels level = levelRepository.findTopByOrderLevelLessThanEqualOrderByOrderLevelDesc(character.getLevel())
                .orElse(null);

        double percentile = rankingInfo.totalPlayers() > 0 ?
                ((rankingInfo.totalPlayers() - rankingInfo.position() + 1.0) / rankingInfo.totalPlayers()) *
                        BusinessConstants.RANKING_PERCENTAGE_MULTIPLIER : BusinessConstants.DEFAULT_PERCENTILE;

        return MyRankingResponse.builder()
                .position(rankingInfo.position())
                .totalPlayers(rankingInfo.totalPlayers())
                .characterName(character.getName())
                .level(character.getLevel())
                .xp(character.getXp())
                .levelName(level != null ? level.getName() : "Unknown")
                .levelTitle(level != null ? level.getTitle() : "Unknown")
                .percentile(Math.round(percentile *  BusinessConstants.RANKING_PERCENTAGE_MULTIPLIER) /
                        BusinessConstants.RANKING_PERCENTAGE_MULTIPLIER)
                .build();
    }
}
