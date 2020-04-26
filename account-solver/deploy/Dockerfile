FROM openjdk:14-jdk-alpine

RUN mkdir /app
COPY ./account-solver-0.0.1-SNAPSHOT.jar /app

ENTRYPOINT ["java"]
CMD ["--enable-preview", "-jar", "/app/account-solver-0.0.1-SNAPSHOT.jar"]

EXPOSE 9001