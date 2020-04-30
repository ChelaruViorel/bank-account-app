#!/bin/bash
mvn clean install -DskipTests=true
java --enable-preview -Dspring.profiles.active=local -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -jar target/account-requester-0.0.1-SNAPSHOT.jar