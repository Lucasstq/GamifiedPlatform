package dev.gamified.GamifiedPlatform.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Configuração do MinIO para armazenamento de arquivos (grimórios).
 */
@Configuration
@Slf4j
public class MinioConfig {

    @Value("${minio.endpoint:http://localhost:9000}")
    private String endpoint;

    @Value("${minio.access-key:minioadmin}")
    private String accessKey;

    @Value("${minio.secret-key:minioadmin}")
    private String secretKey;

    @Value("${minio.bucket.grimoires:grimoires}")
    private String grimoiresBucket;

    /**
     * Cria e configura o cliente MinIO.
     */
    @Bean
    public MinioClient minioClient() {
        log.info("Configuring MinIO client - endpoint: {}", endpoint);

        MinioClient minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();

        // Cria o bucket de grimórios se não existir
        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(grimoiresBucket)
                            .build()
            );

            if (!bucketExists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(grimoiresBucket)
                                .build()
                );
                log.info("MinIO bucket '{}' created successfully", grimoiresBucket);
            } else {
                log.info("MinIO bucket '{}' already exists", grimoiresBucket);
            }

        } catch (Exception e) {
            log.error("Error creating MinIO bucket: {}", e.getMessage(), e);
        }

        return minioClient;
    }
}

