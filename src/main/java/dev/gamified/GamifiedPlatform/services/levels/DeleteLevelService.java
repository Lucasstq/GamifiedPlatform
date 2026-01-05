package dev.gamified.GamifiedPlatform.services.levels;

import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.exceptions.ResourseNotFoundException;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteLevelService {

    private final LevelRepository levelRepository;

    /*
     * Deleta um nível existente na plataforma gamificada.
     * Somente administradores devem ter permissão para executar esta ação.
     */
    public void execute(Long id) {
        Levels level = levelRepository.findById(id)
                .orElseThrow(() -> new ResourseNotFoundException("Level with id " + id + " not found"));
        levelRepository.delete(level);
    }

}
