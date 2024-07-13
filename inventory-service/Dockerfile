# Use an appropriate openjdk:17 base image with Alpine Linux
FROM openjdk:17-alpine

# Update package list and install bind-tools
RUN apk update && apk add bind-tools

# Expose the application port
EXPOSE 9191

# Copy application properties
COPY application.properties /

# Add the JAR file to the image
ADD target/inventory-service.jar inventory-service.jar

# Set the entrypoint for the container
ENTRYPOINT ["java", "-jar", "inventory-service.jar"]
