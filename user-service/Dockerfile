FROM amazoncorretto:21-al2023-headless
WORKDIR /app
COPY target/user-service*.jar /app/app.jar
CMD ["java", "-jar", "app.jar"]