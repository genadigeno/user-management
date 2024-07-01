package user.management.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import user.management.model.User;
import user.management.model.UserOutBox;
import user.management.repository.UserOutBoxRepository;

import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final UserOutBoxRepository userOutBoxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.users.topic}")
    private String usersTopic;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public void send(User user) {
        executorService.submit(() -> {
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(usersTopic, String.valueOf(user.getId()), user);

            future.thenAccept(result -> {
                log.info("Event sent.");
                log.info("deleting from outbox table...");
                UserOutBox userOutBox = userOutBoxRepository.findByUser(user);
                log.info("UserOutBox - {}", userOutBox);
                userOutBoxRepository.delete(userOutBox);
            });

            future.exceptionally(ex -> {
                log.error("event did not send", ex);
                return null;
            });
        });
    }

    public void send(final UserOutBox userOutBox) {
        executorService.submit(() -> {
            User user = userOutBox.getUser();
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(usersTopic, String.valueOf(user.getId()), user);

            future.thenAccept(result -> {
                log.info("Event sent.");
                log.info("deleting from outbox table...");
                log.info("UserOutBox - {}", userOutBox);
                userOutBoxRepository.delete(userOutBox);
            });

            future.exceptionally(ex -> {
                log.error("event did not send", ex);
                return null;
            });
        });
    }

    @PreDestroy
    public void close(){
        executorService.shutdown();
    }
}
