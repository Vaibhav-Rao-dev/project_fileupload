package com.enterprise.storage.presentation.config;

import com.enterprise.storage.domain.ports.api.FileStorageUseCase;
import com.enterprise.storage.domain.ports.spi.FileBinaryRepository;
import com.enterprise.storage.domain.ports.spi.FileMetadataRepository;
import com.enterprise.storage.domain.service.DefaultFileStorageService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

/**
 * Framework-aware configuration class.
 * This bridges the pure Java domain with the Jakarta CDI container.
 */
@ApplicationScoped
public class DomainConfig {

    @Produces
    @ApplicationScoped
    public FileStorageUseCase fileStorageUseCase(
            FileMetadataRepository metadataRepository,
            FileBinaryRepository binaryRepository) {
        
        // We manually inject the infrastructure adapters into the pure domain service
        return new DefaultFileStorageService(metadataRepository, binaryRepository);
    }
}
