package com.enterprise.storage.domain.ports.spi;

import com.enterprise.storage.domain.model.FileMetadataContext;
import java.util.Optional;
import java.util.UUID;

/**
 * SPI Port for persisting and retrieving file structural metadata.
 * Kept strictly clear of infrastructure dependencies.
 */
public interface FileMetadataRepository {
    
    /**
     * Persists the given file metadata context.
     * @param context The immutable domain record containing metadata
     * @return The persisted context
     */
    FileMetadataContext save(FileMetadataContext context);

    /**
     * Retrieves file metadata by its unique identifier.
     * @param id The unique UUID of the file
     * @return An Optional containing the context if found
     */
    Optional<FileMetadataContext> findById(UUID id);

    void updateStatus(com.enterprise.storage.domain.model.FileMetadataContext context);
}
