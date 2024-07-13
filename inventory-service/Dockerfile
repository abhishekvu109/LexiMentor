# Use the openjdk:17 base image
FROM openjdk:17

# Update package list and install dnsutils
RUN apt-get update && apt-get install -y dnsutils

# Expose the application port
EXPOSE 9191

# Copy application properties
COPY application.properties /

# Add the JAR file to the image
ADD target/inventory-service.jar inventory-service.jar

# Set the entrypoint for the container
ENTRYPOINT ["java","-jar","inventory-service.jar"]