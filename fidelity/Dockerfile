FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY fidelity/pom.xml .

RUN mvn dependency:go-offline

COPY fidelity/src ./src

RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]