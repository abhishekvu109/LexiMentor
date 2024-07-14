FROM openjdk:17
# Use a Debian-based OpenJDK 17 image

EXPOSE 9191
COPY application.properties /
ADD target/inventory-service.jar inventory-service.jar

ENTRYPOINT ["java","-jar","inventory-service.jar"]

