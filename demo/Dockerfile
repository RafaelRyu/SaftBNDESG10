FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Aumenta a memória heap para 2 GB (ajuste conforme a memória do host)
ENTRYPOINT ["java", "-Xmx2g", "-Xms512m", "-jar", "app.jar"]