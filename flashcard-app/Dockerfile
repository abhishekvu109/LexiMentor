FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

# Stage 2: Create the final image
FROM openjdk:17
WORKDIR /app
COPY --from=builder /app/target/*.jar flashcard-app.jar
EXPOSE 6060
ENTRYPOINT ["java", "-jar", "flashcard-app.jar"]
