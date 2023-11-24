FROM openjdk:17-ea-5-jdk-alpine

WORKDIR /app

RUN apk --no-cache add curl jq

COPY target/data-actuator-*.jar /app/data-actuator.jar

CMD ["java", "-jar", "data-actuator.jar", "--spring.profiles.active=docker"]