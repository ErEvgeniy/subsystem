FROM openjdk:17-ea-5-jdk-alpine

WORKDIR /app

RUN apk --no-cache add curl jq freetype fontconfig ttf-liberation

COPY target/report-generator-*.jar /app/report-generator.jar

CMD ["java", "-jar", "report-generator.jar", "--spring.profiles.active=docker"]