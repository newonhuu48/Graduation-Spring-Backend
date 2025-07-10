# Use OpenJDK 21 base image
FROM openjdk:21-jdk-slim

# Set working directory inside container
WORKDIR /app

# Copy the jar built by Gradle
COPY build/libs/graduation-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]