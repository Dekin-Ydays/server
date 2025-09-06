# Build stage avec Maven et OpenJDK 21
FROM maven:3.9.4-eclipse-temurin-21 AS build
COPY apifilrouge/ /app/
WORKDIR /app
RUN mvn clean package -DskipTests

# Runtime stage avec OpenJDK 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Installer curl pour health checks (Alpine utilise apk)
RUN apk add --no-cache curl

# Copier le JAR
COPY --from=build /app/target/apifilrouge-*.jar app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8090}/actuator/health || exit 1

# Exposer le port
EXPOSE $PORT

# Démarrer l'application
CMD ["java", "-jar", "app.jar", "--spring.profiles.active=production", "--server.port=${PORT}"]