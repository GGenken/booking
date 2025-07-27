FROM node:20-alpine AS frontend
WORKDIR /frontend

COPY frontend/package*.json ./
RUN npm ci

COPY frontend/ .
RUN npm run build

FROM maven:3.8.5-openjdk-17-slim AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
COPY --from=frontend /frontend/dist/ ./src/main/resources/static/

RUN mvn clean package -DskipTests

RUN cp target/*.jar app.jar

FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/app.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
