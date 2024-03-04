FROM openjdk:17

EXPOSE 9191
COPY application.properties /
ADD target/inventory-service.jar inventory-service.jar

ENTRYPOINT ["java","-jar","inventory-service.jar"]