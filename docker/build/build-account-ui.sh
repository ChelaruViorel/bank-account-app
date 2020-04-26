#!/bin/bash

echo '***********'
echo 'Building Angular account-ui .... '
echo '***********'
echo 
echo

cd ../../account-ui
ng build --prod

echo 
echo
echo '***********'
echo 'Building Docker image account-ui .... '
echo '***********'
echo 
echo
sudo docker build --file=Dockerfile --tag=account-ui:latest --rm=true .

echo 
echo
echo '***********'
echo 'DONE BUILD docker image for account-ui ! '
echo 'checking if image exists ... '
echo '***********'
echo 
echo

sudo docker images | grep account-ui
