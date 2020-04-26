FROM openjdk:14-jdk-alpine

RUN mkdir /app
COPY ./account-requester-0.0.1-SNAPSHOT.jar /app

ENTRYPOINT ["java"]
CMD ["--enable-preview", "-jar", "/app/account-requester-0.0.1-SNAPSHOT.jar"]

EXPOSE 9001