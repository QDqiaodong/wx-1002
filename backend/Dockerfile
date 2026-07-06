FROM docker.io/maven:3.9.6-eclipse-temurin-17 AS builder

RUN mkdir -p /app
WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -Dmaven.repo.local=/root/.m2/repository \
    -Dmaven.wagon.http.ssl.insecure=true \
    -Dmaven.wagon.http.ssl.allowall=true

COPY src ./src

RUN mvn clean package -DskipTests -Dmaven.repo.local=/root/.m2/repository

FROM docker.io/amazoncorretto:17-alpine-jdk

RUN mkdir -p /app/logs

WORKDIR /app

COPY --from=builder /app/target/umbrella-management-1.0.0.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]