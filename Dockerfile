FROM gradle:8.10.2-jdk21 AS build
WORKDIR /app

COPY . .

RUN gradle buildFatJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/build/libs/*-all.jar app.jar

CMD ["java", "-jar", "app.jar"]