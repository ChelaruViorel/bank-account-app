#!/bin/bash
./build-account-requester.sh
./build-account-solver.sh
./build-account-ui.sh

echo 
echo
echo '***********'
echo 'checking if ALL images exist ... '
echo '***********'
echo 
echo
sudo docker images | grep account
