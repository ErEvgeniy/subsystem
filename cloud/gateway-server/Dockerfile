FROM openjdk:17-ea-5-jdk-alpine

WORKDIR /app

RUN apk --no-cache add curl jq

COPY target/gateway-server-*.jar /app/gateway-server.jar

CMD ["java", "-jar", "gateway-server.jar", "--spring.profiles.active=docker"]