FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/NotificationService-0.0.1-SNAPSHOT.jar notification-service.jar
ENTRYPOINT ["java", "-jar", "notification-service.jar"]