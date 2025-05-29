# Verwende offizielles Java 17 Image
FROM eclipse-temurin:17-jdk

# Arbeitsverzeichnis im Container
WORKDIR /app

# Baue die App mit Maven Wrapper
COPY . .
RUN ./mvnw clean package -DskipTests

# Starte das .jar
CMD ["java", "-jar", "target/invoice-scanner-0.0.1-SNAPSHOT.jar"]
