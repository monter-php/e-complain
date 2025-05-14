# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the application source code
COPY src src

# Package the application
RUN mvn package -DskipTests

# Stage 2: Create the final lightweight image
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/target/ecomplain-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
