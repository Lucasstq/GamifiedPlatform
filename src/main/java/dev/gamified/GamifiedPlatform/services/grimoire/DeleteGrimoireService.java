package dev.gamified.GamifiedPlatform.services.grimoire;

import dev.gamified.GamifiedPlatform.domain.Grimoire;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.GrimoireRepository;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteGrimoireService {

    private final MinioClient minioClient;
    private final GrimoireRepository grimoireRepository;

    /*
    * Disponivel apenas para administradores.
     */
    @Transactional
    public void execute(Long levelId) {
        log.info("Deleting grimoire for level {}", levelId);

        Grimoire grimoire = grimoireRepository.findByLevelId(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Grimoire not found for level: " + levelId));

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(grimoire.getMinioBucket())
                            .object(grimoire.getMinioObjectKey())
                            .build()
            );

            grimoireRepository.delete(grimoire);
            log.info("Grimoire deleted successfully - id: {}", grimoire.getId());
        } catch (Exception e) {
            log.error("Error deleting grimoire from MinIO", e);
            throw new BusinessException("Failed to delete grimoire: " + e.getMessage());
        }
    }
}
