
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY build/libs/eventify-backend-*.jar app.jar
RUN mkdir -p /app/logs
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]