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

## Prerequisite tools: 
```
openjdk-14.0.1, node-v12.16.2-linux-x64, docker, docker-compose, psql, mvn, git
```

We are going to install all the tools and this project in the Linux home directory, so set the working directory to ~
```
cd ~
```

Install java
```
wget https://download.java.net/java/GA/jdk14.0.1/664493ef4a6946b186ff29eb326336a2/7/GPL/openjdk-14.0.1_linux-x64_bin.tar.gz
tar -xf openjdk-14.0.1_linux-x64_bin.tar.gz
```

Install node
```
wget https://nodejs.org/dist/v12.16.2/node-v12.16.2-linux-x64.tar.xz
tar -xf node-v12.16.2-linux-x64.tar.xz
```

Open ~/.profile file
```
nano ~/.profile
```

At the end of the file copy these lines and then save the file
```
export JAVA_HOME=~/openjdk-14.0.1
export PATH=~/node-v12.16.2-linux-x64/bin:~/openjdk-14.0.1/bin:$PATH
```

Because you updated the .profile file you normally should restart Ubuntu so that the env variables JAVA_HOME and PATH to be updated for every terminal that you open, but if you don't want to restart you have to remember that in every new terminal that you open first thing you want to do is to run this command `source ~/.profile`

Install docker-compose
```
sudo curl -L "https://github.com/docker/compose/releases/download/1.25.5/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

Let's install the rest of the tools using Ubuntu apt
```
sudo apt install docker
sudo apt install psql
sudo apt install mvn
sudo apt install git
```

At the end of all these instalations the tools's version should look like this:
```
java -version
openjdk version "14.0.1" 2020-04-14
OpenJDK Runtime Environment (build 14.0.1+7)
OpenJDK 64-Bit Server VM (build 14.0.1+7, mixed mode, sharing)

npm version
{
  npm: '6.14.4',
  ares: '1.15.0',
  brotli: '1.0.7',
  cldr: '36.0',
  http_parser: '2.9.3',
  icu: '65.1',
  llhttp: '2.0.4',
  modules: '72',
  napi: '5',
  nghttp2: '1.40.0',
  node: '12.16.2',
  openssl: '1.1.1e',
  tz: '2019c',
  unicode: '12.1',
  uv: '1.34.2',
  v8: '7.8.279.23-node.34',
  zlib: '1.2.11'
}

sudo docker version
Client:
 Version:           18.09.7
 API version:       1.39
 Go version:        go1.10.4
 Git commit:        2d0083d
 Built:             Fri Aug 16 14:19:38 2019
 OS/Arch:           linux/amd64
 Experimental:      false

Server:
 Engine:
  Version:          18.09.7
  API version:      1.39 (minimum version 1.12)
  Go version:       go1.10.4
  Git commit:       2d0083d
  Built:            Thu Aug 15 15:12:41 2019
  OS/Arch:          linux/amd64
  Experimental:     false


sudo docker-compose version
docker-compose version 1.25.5, build 8a1c60f6
docker-py version: 4.1.0
CPython version: 3.7.5
OpenSSL version: OpenSSL 1.1.0l  10 Sep 2019

psql --version
psql (PostgreSQL) 9.5.21

mvn --version
Apache Maven 3.3.9
Maven home: /usr/share/maven
Java version: 14.0.1, vendor: Oracle Corporation
Java home: /home/mimi/.jdks/openjdk-14.0.1
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "4.4.0-166-generic", arch: "amd64", family: "unix"

git --version
git version 2.7.4
```
