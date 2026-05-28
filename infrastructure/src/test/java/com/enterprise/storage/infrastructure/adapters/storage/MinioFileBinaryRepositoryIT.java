package com.enterprise.storage.infrastructure.adapters.storage;

import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class MinioFileBinaryRepositoryIT {

    // Using the core GenericContainer primitive avoids dependency injection issues entirely
    @Container
    private static final GenericContainer<?> minio = new GenericContainer<>("minio/minio:RELEASE.2023-12-14T18-51-57Z")
            .withExposedPorts(9000)
            .withEnv("MINIO_ROOT_USER", "minioadmin")
            .withEnv("MINIO_ROOT_PASSWORD", "minioadmin")
            .withCommand("server /data");

    private static MinioFileBinaryRepository repository;
    private static final String BUCKET = "test-chunks";

    @BeforeAll
    static void setUp() throws Exception {
        // Dynamically construct the URL using the random port assigned by Testcontainers
        String s3Url = "http://" + minio.getHost() + ":" + minio.getMappedPort(9000);
        
        MinioClient client = MinioClient.builder()
                .endpoint(s3Url)
                .credentials("minioadmin", "minioadmin")
                .build();
                
        client.makeBucket(MakeBucketArgs.builder().bucket(BUCKET).build());

        repository = new MinioFileBinaryRepository(
                s3Url,
                "minioadmin",
                "minioadmin",
                BUCKET
        );
    }

    @Test
    void shouldUploadAndDownloadBinaryStream() throws Exception {
        // Given
        UUID fileId = UUID.randomUUID();
        String testData = "Enterprise Grade Microservice Stream Test";
        InputStream uploadStream = new ByteArrayInputStream(testData.getBytes(StandardCharsets.UTF_8));
        long size = testData.length();

        // When
        repository.upload(fileId, uploadStream, size, "text/plain");
        InputStream downloadStream = repository.download(fileId);

        // Then
        String result = new String(downloadStream.readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(testData, result);
    }
}
