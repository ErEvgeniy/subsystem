FROM openjdk:17-ea-5-jdk-alpine

WORKDIR /app

RUN apk --no-cache add curl jq

COPY target/configuration-server-*.jar /app/configuration-server.jar

CMD ["java", "-jar", "configuration-server.jar", "--spring.profiles.active=docker"]
