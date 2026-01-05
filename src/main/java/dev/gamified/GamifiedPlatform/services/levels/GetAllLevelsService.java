package dev.gamified.GamifiedPlatform.services.levels;

import dev.gamified.GamifiedPlatform.dtos.response.LevelResponse;
import dev.gamified.GamifiedPlatform.mapper.LevelMapper;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllLevelsService {

    private final LevelRepository levelRepository;

    /**
     * Lista todos os níveis ordenados por ordem crescente
     */
    public List<LevelResponse> execute() {
        return levelRepository.findAllByOrderByOrderLevelAsc()
                .stream()
                .map(LevelMapper::toResponse)
                .collect(Collectors.toList());
    }
}
