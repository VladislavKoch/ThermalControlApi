FROM eclipse-temurin:17-jre-alpine

COPY /target/*.jar /app.jar

EXPOSE ${API_PORT}
ENTRYPOINT ["java", "-jar", "/app.jar"]

