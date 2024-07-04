package user.management.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import user.management.model.User;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventListener {

    //concurrency = 10 task per instance
    @KafkaListener(
            concurrency = "10",
            topics = "${kafka.users.topic}",
            groupId = "${spring.application.name}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(ConsumerRecord<String, User> message, Acknowledgment ack){
        log.info("Message: key = {}, value = {}", message.key(), message.value());
        ack.acknowledge();
    }
}
