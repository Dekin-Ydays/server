# Build stage avec JDK 23
FROM maven:3.9-openjdk-23-slim AS build
COPY apifilrouge/ /app/
WORKDIR /app
RUN mvn clean package -DskipTests

# Runtime stage avec JDK 23
FROM openjdk:23-jdk-slim
WORKDIR /app

# Installer curl pour health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copier le JAR
COPY --from=build /app/target/apifilrouge-*.jar app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8090}/actuator/health || exit 1

# Exposer le port
EXPOSE $PORT

# Démarrer l'application
CMD ["java", "-jar", "app.jar", "--spring.profiles.active=production", "--server.port=${PORT}"]