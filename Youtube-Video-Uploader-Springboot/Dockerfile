# Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-17 AS builder

# Set working directory
WORKDIR /app

# Copy project files
COPY Youtube-Video-Uploader-Springboot/pom.xml Youtube-Video-Uploader-Springboot/
COPY Youtube-Video-Uploader-Springboot/src/ Youtube-Video-Uploader-Springboot/src/
COPY Youtube-Video-Uploader-Springboot/.mvn/ Youtube-Video-Uploader-Springboot/.mvn/
COPY Youtube-Video-Uploader-Springboot/mvnw Youtube-Video-Uploader-Springboot/mvnw


# 🧡 Fix permissions for mvnw
RUN chmod +x mvnw


# Move inside the Spring Boot project
WORKDIR /app/Youtube-Video-Uploader-Springboot

# Build the app
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the minimal runtime image
FROM eclipse-temurin:17-jre-jammy

# Set working directory
WORKDIR /app

# Copy only the final jar from builder stage
COPY --from=builder /app/Youtube-Video-Uploader-Springboot/target/*.jar Youtube-Video-Uploader.jar

# Expose the app port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "Youtube-Video-Uploader.jar"]
