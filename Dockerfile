FROM openjdk:17-alpine AS login-spring
LABEL authors="김우영"
WORKDIR /app
RUN gradle clean build
COPY ./build/libs/login-0.0.1-SNAPSHOT.jar /app/login.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","login.jar"]