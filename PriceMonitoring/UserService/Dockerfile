FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/UserService-0.0.1-SNAPSHOT.jar user-service.jar
ENTRYPOINT ["java", "-jar", "user-service.jar"]