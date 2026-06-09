FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/EurekaService-0.0.1-SNAPSHOT.jar eureka-service.jar
ENTRYPOINT ["java", "-jar", "eureka-service.jar"]
