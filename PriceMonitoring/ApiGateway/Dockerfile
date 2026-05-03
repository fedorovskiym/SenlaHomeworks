FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/ApiGateway-0.0.1-SNAPSHOT.jar api-gateway.jar
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]
