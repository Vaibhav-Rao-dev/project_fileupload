package com.enterprise.storage.infrastructure.adapters.db;

import com.enterprise.storage.domain.model.FileMetadataContext;
import com.enterprise.storage.domain.model.UploadStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class PostgresFileMetadataRepositoryIT {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("enterprise_db")
            .withUsername("test_user")
            .withPassword("test_pass");

    private static EntityManagerFactory emf;
    private static PostgresFileMetadataRepository repository;

    @BeforeAll
    static void setUp() {
        // 1. Run Flyway Migrations against the Testcontainer
        Flyway flyway = Flyway.configure()
                .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
                .locations("classpath:db/migration")
                .load();
        flyway.migrate();

        // 2. Bootstrap JPA manually using Testcontainer credentials
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.url", postgres.getJdbcUrl());
        properties.put("jakarta.persistence.jdbc.user", postgres.getUsername());
        properties.put("jakarta.persistence.jdbc.password", postgres.getPassword());
        
        emf = Persistence.createEntityManagerFactory("TestPU", properties);
        EntityManager em = emf.createEntityManager();

        // 3. Inject EntityManager into our pure adapter (Using reflection or package-private setter if needed, here we use a workaround for the test)
        repository = new PostgresFileMetadataRepository();
        
        // Simulating the @PersistenceContext injection manually for the test environment
        try {
            java.lang.reflect.Field emField = PostgresFileMetadataRepository.class.getDeclaredField("entityManager");
            emField.setAccessible(true);
            emField.set(repository, em);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject EntityManager", e);
        }
    }

    @AfterAll
    static void tearDown() {
        if (emf != null) {
            emf.close();
        }
    }

    @Test
    void shouldSaveAndRetrieveFileMetadata() {
        // Given
        UUID fileId = UUID.randomUUID();
        FileMetadataContext context = new FileMetadataContext(
                fileId,
                "test-video.mp4",
                "video/mp4",
                1024L * 1024L * 5L, // 5MB
                UploadStatus.INITIATED,
                Instant.now()
        );

        // When (Requires manual transaction management since we lack a Spring/JEE container)
        repository.getEntityManager().getTransaction().begin();
        repository.save(context);
        repository.getEntityManager().getTransaction().commit();

        Optional<FileMetadataContext> retrieved = repository.findById(fileId);

        // Then
        assertTrue(retrieved.isPresent());
        assertEquals("test-video.mp4", retrieved.get().fileName());
        assertEquals(UploadStatus.INITIATED, retrieved.get().status());
    }
}
