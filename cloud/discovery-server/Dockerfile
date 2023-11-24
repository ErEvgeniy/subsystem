FROM openjdk:17-ea-5-jdk-alpine

WORKDIR /app

RUN apk --no-cache add curl jq

COPY target/discovery-server-*.jar /app/discovery-server.jar

CMD ["java", "-jar", "discovery-server.jar", "--spring.profiles.active=docker"]