# --------- Stage 1: Build JAR ----------
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml và download dependencies trước để cache tốt hơn
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code và build
COPY src ./src
RUN mvn clean package -DskipTests

# --------- Stage 2: Runtime ----------
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy file jar từ stage build
COPY --from=build /app/target/*.jar app.jar

# Tạo entrypoint script: ghi GOOGLE_CREDENTIALS vào file
RUN echo '#!/bin/sh' > /entrypoint.sh \
    && echo 'echo "$GOOGLE_CREDENTIALS" > /app/credentials.json' >> /entrypoint.sh \
    && echo 'exec java -jar app.jar' >> /entrypoint.sh \
    && chmod +x /entrypoint.sh

EXPOSE 8080
ENTRYPOINT ["/entrypoint.sh"]
