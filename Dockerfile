# Step 1: Use official lightweight Java 17 image
FROM eclipse-temurin:17-jdk-alpine

# Step 2: Set working directory inside container
WORKDIR /app

# Step 3: Volume for temporary files
VOLUME /tmp

# Step 4: Copy the jar from target folder into container
COPY target/*.jar app.jar

# Step 5: Run Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
