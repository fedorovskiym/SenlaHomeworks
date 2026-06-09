FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/ProductService-0.0.1-SNAPSHOT.jar product-service.jar
ENTRYPOINT ["java", "-jar", "product-service.jar"]