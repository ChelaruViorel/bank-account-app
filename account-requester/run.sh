#!/bin/bash
mvn clean install
java --enable-preview -Dspring.profiles.active=local -jar target/account-requester-0.0.1-SNAPSHOT.jar