# Build stage
FROM gradle:9.3-jdk25 AS build
WORKDIR /app

COPY . .

RUN gradle installDist --no-daemon

# Runtime stage
FROM eclipse-temurin:25-jre-ubi10-minimal
WORKDIR /app

COPY --from=build /app/build/install/* /app/

EXPOSE 8080

CMD ["bin/curatorpharma"]
