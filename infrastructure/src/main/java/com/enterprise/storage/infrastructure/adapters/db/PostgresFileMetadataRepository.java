package com.enterprise.storage.infrastructure.adapters.db;

import com.enterprise.storage.domain.model.FileMetadataContext;
import com.enterprise.storage.domain.model.UploadStatus;
import com.enterprise.storage.domain.ports.spi.FileMetadataRepository;
import com.enterprise.storage.infrastructure.adapters.db.jpa.FileMetadataEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PostgresFileMetadataRepository implements FileMetadataRepository {

    @PersistenceContext
    private EntityManager entityManager;

    // --- Visible for Testing ---
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    // ---------------------------

    @Override
    @Transactional
    public FileMetadataContext save(FileMetadataContext context) {
        FileMetadataEntity entity = new FileMetadataEntity();
        entity.setId(context.id());
        entity.setFileName(context.fileName());
        entity.setContentType(context.contentType());
        entity.setSizeBytes(context.sizeBytes());
        entity.setStatus(context.status().name());
        entity.setCreatedAt(context.createdAt());
        entityManager.persist(entity);
        return context; 
    }

    @Override
    public Optional<FileMetadataContext> findById(UUID id) {
        FileMetadataEntity entity = entityManager.find(FileMetadataEntity.class, id);
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(new FileMetadataContext(
                entity.getId(),
                entity.getFileName(),
                entity.getContentType(),
                entity.getSizeBytes(),
                UploadStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt()
        ));
    }

    @Override
    @Transactional
    public void updateStatus(FileMetadataContext context) {
        FileMetadataEntity entity = entityManager.find(FileMetadataEntity.class, context.id());
        if (entity != null) {
            entity.setStatus(context.status().name());
            entityManager.merge(entity);
        }
    }
}
