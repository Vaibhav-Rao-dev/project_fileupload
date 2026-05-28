package com.enterprise.storage.domain.ports.spi;

import java.io.InputStream;
import java.util.UUID;

/**
 * SPI Port for writing and reading raw binary object payloads.
 */
public interface FileBinaryRepository {
    
    /**
     * Uploads a raw input stream payload to the physical object store.
     * * @param objectKey Unique identifier/key for the storage path (typically UUID)
     * @param inputStream Source stream of the file payload
     * @param sizeBytes Expected size of the file stream
     * @param contentType MIME type of the file
     */
    void upload(UUID objectKey, InputStream inputStream, long sizeBytes, String contentType);

    /**
     * Downloads a physical object stream from the store.
     * * @param objectKey Unique identifier of the file
     * @return InputStream containing the raw file data
     */
    InputStream download(UUID objectKey);
}
