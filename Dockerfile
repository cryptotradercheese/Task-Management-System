FROM gradle:8.5.0-jdk17 AS build
COPY . /app
WORKDIR /app
RUN gradle bootJar

FROM openjdk:17-jdk-alpine
COPY --from=build /app/build/libs/*.jar /app/spring-boot-application.jar
ENTRYPOINT ["java", "-jar", "/app/spring-boot-application.jar"]