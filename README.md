# bank-account-app
Demo application exercise for creating a bank account for existing clients.
A user which is the bank's client, should be able to access a page to open a new savings bank account. The opening of the account can happen only Monday-Friday and between hours 9-18.

We assume that the user is already logged, so we are not interested in the authentication.

We concentrate our exercise only on the opening of the account. We make a clear distinction between acknowledging the request of the client and the actual opening of the account. Because we, internally, have the restriction that we cannot open an account anytime but only in specific days and hours, this doesn't mean that we respond to the client that we cannot honour his request for exampple on a Sunday at 13:00. We will take his request any time 24/7, and we inform him that his request to create an account was acknowledged and he will be informed when the account is created. Meanwhile we will create the account in the background according to the internal restrictions.

We can achieve this separation of requesting an account and the actual creation of the account by implementing 2 separated backend microservices called account-requester and account-solver. The account-requester will take the user's request and send it as a message on a Kafka topic, which will be processed to insert an account request record in a Postgresql database. And here the job of the account-requester is done. Then, the account-solver will pick an account request and will start to solved the request by doing whatever necessary to create a bank account, which in our case is just the creation of a record in the accounts table in the database because this is just aan example. Also, this separation of the account request creation and solving into 2 different microservices will allow us to scale them granularly.

This activity is summarized in the following diagram. All the components of this architecture are deployed as docker containers so the diagram will show the interaction betweeken these docker containers.
![Bank account app architecture](diagram/bank-account-app.png)

The docker containers are implemented as follows:  

Kafka, Zookeeper, Postgres containers run docker images found on https://hub.docker.com. Check the docker-compose file /docker/deploy/ing.yml to see exactly what image versions I used.

Account Requester and Account Solver containers run the docker images built from the SpringBoot applications that I implemented: account-requester and account-solver.

Account UI container runs the image built from the Angular application that I implemented: account-ui.


# How to build & deploy the applications locally on your Ubuntu Linux computer

Prerequisite tools: openjdk-14.0.1, node-v12.16.2-linux-x64, docker, docker-compose, psql, mvn, git

We are going to install all the tools and this project in the Linux home directory, so set the working directory to ~
```
cd ~
```

Install java
```
wget https://download.java.net/java/GA/jdk14.0.1/664493ef4a6946b186ff29eb326336a2/7/GPL/openjdk-14.0.1_linux-x64_bin.tar.gz
tar -xf openjdk-14.0.1_linux-x64_bin.tar.gz
```
