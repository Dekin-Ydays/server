# Build stage with Maven and OpenJDK 21
FROM maven:3.9.4-eclipse-temurin-21 AS build
COPY apifilrouge/ /app/
WORKDIR /app
RUN mvn clean package -DskipTests

# Runtime stage with OpenJDK 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Install curl for health checks
RUN apk add --no-cache curl

# Copy the JAR
COPY --from=build /app/target/apifilrouge-*.jar app.jar

ENV PORT=8090

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${PORT}/actuator/health || exit 1

EXPOSE ${PORT}

USER appuser

# Use shell form so ${PORT} is expanded
CMD java -jar app.jar --spring.profiles.active=prod --server.port=${PORT}
