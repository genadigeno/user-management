# User Management Microservice 
## Project Description
User Management Service is the project implements Event-Driven Architecture.</br>
It provides CRUD operations for a user.</br>
It uses kafka message broker where it sends event about user creation.
To overcome Dual Writing problem we also implement Outbox pattern.

### Visual
![alt text](./diagram.png)

### Installation requirements
In order to start up the application you need have installed:
- Docker on you machine </br>
- git </br>
  or
- Apache kafka cluster with at least two brokers
- git
- postgres DB 13+
- gradle
- JDK-17

### Installation steps
The project installation could be done using docker-compose.yml via command line interface (CMD):
```
git clone https://github.com/genadigeno/user-management.git &&
cd user-management &&
docker compose up
```
or
```
git clone https://github.com/genadigeno/user-management.git &&
cd user-management &&
./gradleview clean bootJar &&
java -jar ./build/libs/user-management.jar
```
### JVM Parameters
- `SERVER_PORT` - application port number, default: 8282
- `KAFKA_BOOTSTRAP_SERVERS` - kafka cluster url, default: localhost:9092,localhost:9093
- `KAFKA_USER_TOPIC_NAME` - kafka topic name, default: users.topic
- `KAFKA_USER_DLT_TOPIC_NAME` - kafka dead letter topic name, default: users.topic
- `POSTGRES_URL` - postgres url
- `POSTGRES_USER` - postgres user
- `POSTGRES_PASSWORD` - postgres password
- `JWT_SECRET_KEY` - jwt secret key
#### Example `java -DPOSTGRES_URL=jdbc:postgresql://localhost:5432/user_management?currentSchema=gvggroup -DPOSTGRES_USER=postgres -DPOSTGRES_PASSWORD=secret -jar ./target/accident-event-stream.jar`
