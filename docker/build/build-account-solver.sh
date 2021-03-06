#!/bin/bash

echo '***********'
echo 'Building Spring Boot account-solver .... '
echo '***********'
echo 
echo

cd ../../account-solver
mvn clean install

echo 
echo
echo '***********'
echo 'Building Docker image account-solver .... '
echo '***********'
echo 
echo
cd deploy
sudo docker build --file=Dockerfile --tag=account-solver:latest --rm=true .

echo 
echo
echo '***********'
echo 'DONE BUILD docker image for account-solver ! '
echo 'checking if image exists ... '
echo '***********'
echo 
echo

sudo docker images | grep account-solver
