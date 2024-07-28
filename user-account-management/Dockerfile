FROM harbor.pepc.rw/library/openjdk:17

ENV SPRING_PROFILES_ACTIVE prod

WORKDIR /app

COPY target/userAccount-ms-0.0.1-SNAPSHOT.jar userAccount-ms.jar

EXPOSE 9000

ENTRYPOINT ["java", "-jar", "userAccount-ms.jar"]


# docker build -t Schedules .