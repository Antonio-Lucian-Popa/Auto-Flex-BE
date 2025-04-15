FROM arm64v8/eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw \
 && ./mvnw clean package -DskipTests

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "target/AutoFlex-0.0.1-SNAPSHOT.jar"]
