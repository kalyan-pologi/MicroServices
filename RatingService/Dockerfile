# Use OpenJDK 21 as base image
FROM openjdk:21-jdk

# Set working directory inside the container
WORKDIR /app

# Copy your built Spring Boot jar into the container
COPY target/rating-service.jar app.jar

# Expose the port that your application uses (match application.properties)
EXPOSE 8083

# Command to run your Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
