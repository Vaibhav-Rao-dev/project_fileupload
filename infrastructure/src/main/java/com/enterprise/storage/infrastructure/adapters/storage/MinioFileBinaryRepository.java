package com.enterprise.storage.infrastructure.adapters.storage;

import com.enterprise.storage.domain.ports.spi.FileBinaryRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;
import java.util.function.Supplier;

public class MinioFileBinaryRepository implements FileBinaryRepository {

    private static final Logger log = LoggerFactory.getLogger(MinioFileBinaryRepository.class);
    
    private final MinioClient minioClient;
    private final String bucketName;
    private final CircuitBreaker circuitBreaker;

    public MinioFileBinaryRepository(String endpoint, String accessKey, String secretKey, String bucketName) {
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        this.bucketName = bucketName;

        // Configure strict enterprise limits: Trip after 2 consecutive failures
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .minimumNumberOfCalls(2)
                .slidingWindowSize(4)
                .waitDurationInOpenState(Duration.ofSeconds(20)) // Stay dead for 20 seconds
                .build();
                
        this.circuitBreaker = CircuitBreaker.of("minio-storage", config);

        // Attach an Event Listener to explicitly log when the state changes
        this.circuitBreaker.getEventPublisher()
                .onStateTransition(event -> log.warn("🚨 [CIRCUIT BREAKER] STATE CHANGED: {} 🚨", event.getStateTransition()));
    }

    @Override
    public void upload(UUID fileId, InputStream chunkStream, long chunkSize, String contentType) {
        Runnable uploadTask = () -> {
            try {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileId.toString())
                                .stream(chunkStream, chunkSize, -1)
                                .contentType(contentType)
                                .build()
                );
            } catch (Exception e) {
                throw new RuntimeException("Storage network failure during upload", e);
            }
        };
        // Wrap the network call in the Circuit Breaker
        circuitBreaker.executeRunnable(uploadTask);
    }

    // FIX: Restored the download method to satisfy the Domain Interface Contract
    @Override
    public InputStream download(UUID fileId) {
        Supplier<InputStream> downloadTask = () -> {
            try {
                return minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileId.toString())
                                .build()
                );
            } catch (Exception e) {
                throw new RuntimeException("Storage network failure during download", e);
            }
        };
        // Wrap the download call in the Circuit Breaker as well
        return circuitBreaker.executeSupplier(downloadTask);
    }
}
