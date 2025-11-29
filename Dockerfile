# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy layered JAR
COPY --from=builder /app/target/internet-store-*.jar app.jar

# Create non-root user
RUN groupadd -r spring && useradd -r -g spring spring
USER spring

# JVM options
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]