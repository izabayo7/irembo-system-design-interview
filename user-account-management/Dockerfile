# First stage: Build the application
FROM maven:3.8.4-openjdk-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

FROM harbor.pepc.rw/library/openjdk:17

ENV SPRING_PROFILES_ACTIVE prod

WORKDIR /app

COPY --from=build /app/target/userAccount-ms.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]