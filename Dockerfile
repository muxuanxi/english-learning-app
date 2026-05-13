FROM eclipse-temurin:23-jre

WORKDIR /app

COPY backend/target/english-learn-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
