# ==========================================
# STAGE 1: JLink Custom JVM Builder
# ==========================================
FROM eclipse-temurin:21-jdk-alpine3.20 AS jre-builder

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
FROM eclipse-temurin:21-jre-alpine3.20 AS layer-extractor
WORKDIR /build
COPY bootstrap/target/bootstrap-1.0.0.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# ==========================================
# STAGE 3: Production Micro-Image
# ==========================================
FROM alpine:3.20

ENV JAVA_HOME=/jre
ENV PATH="$JAVA_HOME/bin:$PATH"
COPY --from=jre-builder /custom-jre $JAVA_HOME

RUN addgroup -S enterprise && adduser -S spring -G enterprise
USER spring:enterprise
WORKDIR /app

COPY --from=layer-extractor /build/dependencies/ ./
COPY --from=layer-extractor /build/spring-boot-loader/ ./
COPY --from=layer-extractor /build/snapshot-dependencies/ ./
COPY --from=layer-extractor /build/application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
