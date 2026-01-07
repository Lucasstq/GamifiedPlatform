package dev.gamified.GamifiedPlatform.services.levels;

import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.dtos.request.LevelRequest;
import dev.gamified.GamifiedPlatform.dtos.response.LevelResponse;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.mapper.LevelMapper;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por criar novos níveis na plataforma gamificada.
 * Apenas administradores podem criar níveis (verificação feita no controller via @IsAdmin).
 */
@Service
@RequiredArgsConstructor
public class CreateLevelService {

    private final LevelRepository levelRepository;

    /**
     * Cria um novo nível na jornada épica de aprendizado.
     * Disponível apenas para administradores (verificação feita no controller).
     *
     * @param request dados do nível a ser criado
     * @return o nível criado
     * @throws BusinessException se já existir nível com o mesmo orderLevel ou se o XP não seguir progressão correta
     */
    @Transactional
    public LevelResponse execute(LevelRequest request) {

        // Verificar se já existe nível com o mesmo orderLevel
        validateOrderLevelDoesNotExist(request.orderLevel());

        // Validar progressão de XP em relação ao nível anterior
        validateXpProgression(request.orderLevel(), request.xpRequired());

        // Mapear LevelRequest para Levels
        Levels level = LevelMapper.toEntity(request);

        try {
            // Salvar o nível no repositório
            Levels savedLevel = levelRepository.save(level);

            // Mapear Levels para LevelResponse
            return LevelMapper.toResponse(savedLevel);
        } catch (DataIntegrityViolationException e) {
            // Fallback para race conditions: captura violações de integridade que podem ocorrer
            // entre a validação prévia e o momento do save em ambientes concorrentes
            throw new BusinessException("Level with order " + request.orderLevel() + " already exists or data integrity violation occurred");
        }
    }

    private void validateOrderLevelDoesNotExist(Integer orderLevel) {
        if (levelRepository.existsByOrderLevel(orderLevel)) {
            throw new BusinessException("Level with order " + orderLevel + " already exists");
        }
    }

    private void validateXpProgression(Integer orderLevel, Integer xpRequired) {
        // Buscar o nível anterior para validar progressão de XP
        // Se não existir nível anterior (orderLevel == 1), a validação não é aplicada
        levelRepository.findByOrderLevel(orderLevel - 1).ifPresent(previousLevel -> {
            if (xpRequired <= previousLevel.getXpRequired()) {
                throw new BusinessException(
                        "XP required for level " + orderLevel + " (" + xpRequired + ") " +
                                "must be greater than previous level (" + previousLevel.getXpRequired() + ")"
                );
            }
        });
    }

}
