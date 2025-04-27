# Step 1: Use an official OpenJDK image
FROM openjdk:17-jdk-slim

# Step 2: Set working directory
WORKDIR /app

# Step 3: Copy built jar file into container
COPY target/*.jar app.jar

# Step 4: Expose port 8080
EXPOSE 8080

# Step 5: Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
