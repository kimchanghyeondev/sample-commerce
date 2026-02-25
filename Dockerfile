FROM gradle:8.5-jdk17 AS build

WORKDIR /app
COPY . .
RUN gradle clean build -x test --no-daemon

FROM eclipse-temurin:17-jre

ARG MODULE
WORKDIR /app
COPY --from=build /app/${MODULE}/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
