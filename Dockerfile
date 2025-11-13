FROM gradle:8.5-jdk21 AS BUILDER
WORKDIR /app
COPY . .

RUN gradle clean build

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=BUILDER /app/build/libs/speech-service-1.0-SNAPSHOT.jar .
EXPOSE 8080

CMD ["java", "-jar", "speech-service-1.0-SNAPSHOT.jar"]