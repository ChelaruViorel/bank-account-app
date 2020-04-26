#!/bin/bash

echo '***********'
echo 'Building Spring Boot account-requester .... '
echo '***********'
echo 
echo

cd ../../account-requester
mvn clean install

echo 
echo
echo '***********'
echo 'Building Docker image account-requester .... '
echo '***********'
echo 
echo
cd deploy
sudo docker build --file=Dockerfile --tag=account-requester:latest --rm=true .

echo 
echo
echo '***********'
echo 'DONE BUILD docker image for account-requester ! '
echo 'checking if image exists ... '
echo '***********'
echo 
echo

sudo docker images | grep account-requester
