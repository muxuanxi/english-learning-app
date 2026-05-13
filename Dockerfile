# Multi-stage build: build backend + frontend in one image
FROM maven:3.9-eclipse-temurin-23 AS backend-build
WORKDIR /app
COPY backend/pom.xml .
RUN mvn dependency:go-offline -q
COPY backend/src ./src
COPY frontend/dist ./src/main/resources/static
RUN mvn package -DskipTests -q

FROM eclipse-temurin:23-jre
WORKDIR /app
COPY --from=backend-build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]
