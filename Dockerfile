FROM openjdk:17-alpine

RUN mkdir "app"
WORKDIR /app

COPY ./build/libs/user-management.jar .

ENV SERVER_PORT=8282
ENV KAFKA_BOOTSTRAP_SERVERS='localhost:9092,localhost:9093'

ENV KAFKA_USER_TOPIC_NAME='users.topic'
ENV KAFKA_USER_DLT_TOPIC_NAME='users.topic.dlt'

ENV POSTGRES_URL='jdbc:postgresql://localhost:5432/user_management?currentSchema=gvggroup'
ENV POSTGRES_USER='postgres'
ENV POSTGRES_PASSWORD=''

EXPOSE ${SERVER_PORT}

ENTRYPOINT ["java", "-jar"]

CMD ["user-management.jar"]