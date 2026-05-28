# ==========================================
# STAGE 1: JLink Custom JVM Builder
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS jre-builder

# Use jlink to build a stripped-down, custom Java Runtime Environment
RUN $JAVA_HOME/bin/jlink \
    --add-modules java.base,java.logging,java.naming,java.sql,java.xml,java.management,java.security.jgss,java.instrument,java.desktop,java.prefs,jdk.crypto.ec,jdk.unsupported \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /custom-jre

# ==========================================
# STAGE 2: Spring Boot Layer Extractor
# ==========================================
FROM eclipse-temurin:21-jre-alpine AS layer-extractor
WORKDIR /build
COPY bootstrap/target/bootstrap-1.0.0.jar app.jar
# Extract the fat jar into optimized caching layers
RUN java -Djarmode=layertools -jar app.jar extract

# ==========================================
# STAGE 3: Production Micro-Image
# ==========================================
FROM alpine:3.19

# Set our custom JVM as the system Java
ENV JAVA_HOME=/jre
ENV PATH="$JAVA_HOME/bin:$PATH"
COPY --from=jre-builder /custom-jre $JAVA_HOME

# Enterprise Security: Run as a non-root user
RUN addgroup -S enterprise && adduser -S spring -G enterprise
USER spring:enterprise
WORKDIR /app

# Copy the extracted layers in strict order from least-frequently-changed to most
COPY --from=layer-extractor /build/dependencies/ ./
COPY --from=layer-extractor /build/spring-boot-loader/ ./
COPY --from=layer-extractor /build/snapshot-dependencies/ ./
COPY --from=layer-extractor /build/application/ ./

EXPOSE 8080

# Launch utilizing the Spring Boot Layered Launcher
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
