FROM openjdk:11-jre-slim
ARG JAR_FILE=build/libs/k5smovie-movie-chat.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["nohup","java","-jar","/app.jar","&"]