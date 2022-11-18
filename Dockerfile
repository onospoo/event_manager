FROM gradle:7.5.1-jdk11-alpine

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

EXPOSE 8080

ENTRYPOINT ["java","-jar","/home/gradle/src/build/libs/event_manager-0.0.1-SNAPSHOT.jar"]
