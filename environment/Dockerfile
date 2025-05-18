FROM openjdk:17.0.1-jdk-slim
LABEL author="phucth"
ENV PORT=9191
COPY learnx-be-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]