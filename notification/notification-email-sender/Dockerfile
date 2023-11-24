FROM openjdk:17-ea-5-jdk-alpine

WORKDIR /app

RUN apk --no-cache add curl jq

COPY target/notification-email-sender-*.jar /app/notification-email-sender.jar

CMD ["java", "-jar", "notification-email-sender.jar", "--spring.profiles.active=docker"]