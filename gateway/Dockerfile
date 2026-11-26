FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "echo 'Waiting before starting gateway...' && sleep 10 && java -jar /app/app.jar"]