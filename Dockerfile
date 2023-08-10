#
# Build stage
#
FROM maven:3.9.0-eclipse-temurin-17-alpine AS build
COPY . .
RUN mvn clean package -DskipTests

#
#Package stage
#
FROM openjdk:17
COPY --from=build /target/Externo-0.0.1-SNAPSHOT.jar Externo-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","Externo-0.0.1-SNAPSHOT.jar"]
