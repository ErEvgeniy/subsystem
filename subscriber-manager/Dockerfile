FROM openjdk:17-ea-5-jdk-alpine

WORKDIR /app

RUN apk --no-cache add curl jq

COPY target/subscriber-manager-*.jar /app/subscriber-manager.jar

CMD ["java", "-jar", "subscriber-manager.jar", "--spring.profiles.active=docker"]