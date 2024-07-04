package user.management.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;

@Slf4j
@RequiredArgsConstructor
public class UserRecordRecoverer implements ConsumerRecordRecoverer {
    private final String dlt;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void accept(ConsumerRecord<?, ?> consumerRecord, Exception exception) {
        log.warn("Error - {}, value - {}, key - {}",
                exception.getMessage(),
                consumerRecord.value().toString(),
                consumerRecord.key().toString());

        kafkaTemplate.send(dlt, consumerRecord.key().toString(), consumerRecord.value().toString());
    }
}
