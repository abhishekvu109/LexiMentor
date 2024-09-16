FROM openjdk:17
# Use a Debian-based OpenJDK 17 image

EXPOSE 6565
ADD target/llm-service.jar llm-service.jar

ENTRYPOINT ["java", "-jar", "llm-service.jar"]