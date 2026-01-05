package dev.gamified.GamifiedPlatform.services.levels;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.dtos.response.LevelResponse;
import dev.gamified.GamifiedPlatform.exceptions.AcessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.ResourseNotFoundException;
import dev.gamified.GamifiedPlatform.repository.PlayerCharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LevelByUserAuthenticate {

    private final PlayerCharacterRepository playerCharacterRepository;
    private final CalculateLevelByXpService calculateLevelByXp;

    /**
     * Busca o nível atual do personagem do usuário autenticado.
     * Retorna os detalhes do nível alcançado baseado no XP do personagem.
     */
    public LevelResponse execute(Long userId) {
        validateResourceOwnerOrAdmin(userId);

        PlayerCharacter character = playerCharacterRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourseNotFoundException("Character not found for user with id: " + userId));

        // Calcula qual nível da tabela de níveis o personagem alcançou baseado no XP
        return calculateLevelByXp.execute(character.getXp());
    }

    private void validateResourceOwnerOrAdmin(Long userId) {
        if (!SecurityUtils.isResourceOwnerOrAdmin(userId)) {
            throw new AcessDeniedException("You do not have permission to access this user's level information");
        }
    }

}
