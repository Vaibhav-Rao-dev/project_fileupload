package com.enterprise.storage.bootstrap.config;

import com.enterprise.storage.domain.ports.api.FileStorageUseCase;
import com.enterprise.storage.domain.ports.spi.FileBinaryRepository;
import com.enterprise.storage.domain.ports.spi.FileMetadataRepository;
import com.enterprise.storage.domain.service.DefaultFileStorageService;
import com.enterprise.storage.infrastructure.adapters.db.PostgresFileMetadataRepository;
import com.enterprise.storage.infrastructure.adapters.storage.MinioFileBinaryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class acts as the translator between our pure Hexagonal architecture and Spring Boot.
 * It manually instantiates our adapters and injects them into the pure domain service.
 */
@Configuration
public class HexagonalWiringConfig {

    @Bean
    public FileMetadataRepository fileMetadataRepository() {
        // Spring's BeanPostProcessor will automatically satisfy the @PersistenceContext annotation inside this instance
        return new PostgresFileMetadataRepository();
    }

    @Bean
    public FileBinaryRepository fileBinaryRepository(
            @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey,
            @Value("${minio.bucket-name}") String bucketName) {
        return new MinioFileBinaryRepository(endpoint, accessKey, secretKey, bucketName);
    }

    @Bean
    public FileStorageUseCase fileStorageUseCase(
            FileMetadataRepository metadataRepository,
            FileBinaryRepository binaryRepository) {
        // We inject the Spring-managed adapters into our framework-agnostic domain service
        return new DefaultFileStorageService(metadataRepository, binaryRepository);
    }
}
