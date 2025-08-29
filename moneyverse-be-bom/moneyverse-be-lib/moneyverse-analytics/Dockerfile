FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn -B -f moneyverse-be-lib/pom.xml -pl moneyverse-analytics -am clean package -DskipTests

FROM bellsoft/liberica-openjdk-alpine:21
WORKDIR /app
COPY --from=builder /app/moneyverse-be-lib/moneyverse-analytics/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]