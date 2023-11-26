FROM openjdk:17-ea-5-jdk-alpine

WORKDIR /app

RUN apk --no-cache add curl jq

COPY target/notification-sms-sender-*.jar /app/notification-sms-sender.jar

CMD ["java", "-jar", "notification-sms-sender.jar", "--spring.profiles.active=docker"]