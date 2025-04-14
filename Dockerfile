# Faza de build
FROM eclipse-temurin:17-jdk-alpine as build

WORKDIR /app
COPY . .

RUN ./mvnw clean package -DskipTests

# Faza finală
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# activează profilul "prod"
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app/app.jar"]
