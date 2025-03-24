FROM openjdk:17
# Use a Debian-based OpenJDK 17 image

EXPOSE 8989
COPY ./target/classes/application.properties /
ADD target/writewise-learning-service.jar writewise-learning-service.jar

ENTRYPOINT ["java", "-jar", "writewise-learning-service.jar"]