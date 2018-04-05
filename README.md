# Java Workshop
Cloud configuration

## Install
#### Requires the following:
- Maven 4.0
- Java 1.8
- Docker

##configuration-service
### build
- cd ./configuration-service
- mvn clean package
### run
- cd ./configuration-service
- java -jar ./target/configuration-service-0.0.1-SNAPSHOT.jar
### verify
- http://localhost:8088/reservation-service/master

##eureka-server
### build
- cd ./eureka-server
- mvn clean package
### run
- cd ./eureka-server
- java -jar ./target/eureka-server-0.0.1-SNAPSHOT.jar
### verify
- http://localhost:8761/

##rabbit-mq
###run
- docker run --rm -i -t --hostname rabbit --name rabbit -p 5672:5672 -p 8010:15672 rabbitmq:3-management
###verify 
- http://localhost:8010/
  
### 
##reservation-service
### build
- cd ./reservation-service
- mvn clean package
### run
- cd ./reservation-service
- java -jar ./target/reservation-service-0.0.1-SNAPSHOT.jar
### verify
- http://localhost:8081/reservations

##reservation-client
### build
- cd ./reservation-client
- mvn clean package
### run
- cd ./reservation-client
- java -jar ./target/reservation-client-0.0.1-SNAPSHOT.jar
### verify
- http://localhost:8079/reservations/names
### add record 
- POST string to http://localhost:8079/reservations