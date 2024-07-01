package user.management.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import user.management.model.UserOutBox;
import user.management.repository.UserOutBoxRepository;
import user.management.service.NotificationService;

import java.util.List;

@Slf4j
@EnableScheduling
@Configuration
@RequiredArgsConstructor
public class BatchProcessingConfig {
    private final UserOutBoxRepository userOutBoxRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 * * * * *")
    public void extractDataFromOutboxTable(){
        log.info("Retrieving data from outbox table...");
        List<UserOutBox> users = userOutBoxRepository.findAll();
        log.info("total records - {}", users.size());
        users.forEach(this::sendEvent);
    }

    private void sendEvent(UserOutBox userOutBox) {
        log.info("Sending event for userOutBox - {}", userOutBox.getId());
        notificationService.send(userOutBox);
    }
}
