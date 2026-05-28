package com.enterprise.storage.domain.service;

import com.enterprise.storage.domain.annotations.LogExecutionTime;
import com.enterprise.storage.domain.model.FileMetadataContext;
import com.enterprise.storage.domain.model.UploadStatus;
import com.enterprise.storage.domain.ports.api.FileStorageUseCase;
import com.enterprise.storage.domain.ports.spi.FileBinaryRepository;
import com.enterprise.storage.domain.ports.spi.FileMetadataRepository;

import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;

public class DefaultFileStorageService implements FileStorageUseCase {

    private final FileMetadataRepository metadataRepository;
    private final FileBinaryRepository binaryRepository;

    public DefaultFileStorageService(FileMetadataRepository metadataRepository, FileBinaryRepository binaryRepository) {
        this.metadataRepository = metadataRepository;
        this.binaryRepository = binaryRepository;
    }

    @Override
    @LogExecutionTime
    public FileMetadataContext initiateUpload(String fileName, String contentType, long sizeBytes) {
        if (sizeBytes <= 0) {
            throw new IllegalArgumentException("File size must be strictly positive.");
        }
        FileMetadataContext context = new FileMetadataContext(
                UUID.randomUUID(), fileName, contentType, sizeBytes, UploadStatus.INITIATED, Instant.now()
        );
        metadataRepository.save(context);
        return context;
    }

    @Override
    @LogExecutionTime
    public void uploadChunk(UUID fileId, InputStream chunkStream, long chunkSize) {
        FileMetadataContext context = metadataRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown File ID"));

        binaryRepository.upload(fileId, chunkStream, chunkSize, context.contentType());
        
        FileMetadataContext updatedContext = new FileMetadataContext(
                context.id(), context.fileName(), context.contentType(), context.sizeBytes(),
                UploadStatus.UPLOADING, context.createdAt()
        );
        metadataRepository.updateStatus(updatedContext);
    }

    @Override
    @LogExecutionTime
    public FileMetadataContext completeUpload(UUID fileId) {
        FileMetadataContext context = metadataRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown File ID"));

        FileMetadataContext completedContext = new FileMetadataContext(
                context.id(), context.fileName(), context.contentType(), context.sizeBytes(),
                UploadStatus.COMPLETED, context.createdAt()
        );
        metadataRepository.updateStatus(completedContext);
        return completedContext;
    }
}
