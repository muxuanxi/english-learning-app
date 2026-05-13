# Multi-stage build: build frontend + backend, then package into minimal runtime
# Stage 1: Build Vue frontend
FROM node:20-alpine AS frontend-build
WORKDIR /app
COPY frontend/package*.json ./
RUN npm ci --production=false 2>/dev/null || npm install
COPY frontend/ ./
RUN npm run build

# Stage 2: Build Spring Boot backend + include frontend static files
FROM maven:3.9-eclipse-temurin-23 AS backend-build
WORKDIR /app
COPY backend/pom.xml .
RUN mvn dependency:go-offline -q
COPY backend/src ./src
COPY --from=frontend-build /app/dist ./src/main/resources/static
RUN mvn package -DskipTests -q

# Stage 3: Minimal JRE runtime
FROM eclipse-temurin:23-jre
WORKDIR /app
COPY --from=backend-build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]
