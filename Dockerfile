FROM eclipse-temurin:25-jdk-alpine
COPY ./target/ragbot-*.jar /opt/app/bot.jar
ENTRYPOINT ["java", "-jar", "/opt/app/bot.jar"]