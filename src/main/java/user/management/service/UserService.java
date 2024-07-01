package user.management.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import user.management.dto.PageDto;
import user.management.dto.UserDto;
import user.management.dto.http.UserCreateRequest;
import user.management.dto.http.UserRequest;
import user.management.exception.UserAlreadyExistsException;
import user.management.exception.UserNotFoundException;
import user.management.model.User;
import user.management.model.UserOutBox;
import user.management.repository.UserOutBoxRepository;
import user.management.repository.UserRepository;
import user.management.utils.UserMapper;

import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserOutBoxRepository userOutBoxRepository;

    private final HazelcastInstance hazelcastInstance;
    private final NotificationService notificationService;

    public PageDto getUsers(int size, int page) {
        log.info("Get users request for page {} with size {}", page, size);
        Page<UserDto> users = userRepository.findAll(PageRequest.of(page, size)).map(UserMapper::map);
        return PageDto.builder()
                .data(users.getContent())
                .total(users.getTotalElements())
                .build();
    }

    public UserDto getUser(int id) {
        log.info("Get user request for id {}", id);

        IMap<Integer, User> userCacheMap = getUserCacheMap();

        log.info("Retrieving from cache...");
        User user = userCacheMap.get(id);
        if (user == null){
            log.info("Cache is empty. Retrieving from database...");
            user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
            userCacheMap.put(user.getId(), user);
            log.info("Inserted into cache.");
        }

        return UserMapper.map(user);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String createUser(UserCreateRequest request) {
        long count = userRepository.countByUsername(request.getUsername());
        if (count > 0) {
            log.warn("User with username {} already exists", request.getUsername());
            throw new UserAlreadyExistsException();
        }

        User saved = userRepository.save(UserMapper.map(request));
        log.info("User with id has been created");

        getUserCacheMap().put(saved.getId(), saved);
        log.info("Inserted into cache");

        //insert into outbox table
        saveToOutBoxTable(saved);

        sendEvent(saved);//different transaction scope

        return "created with id: "+saved.getId();
    }

    private void sendEvent(User user){
        log.info("Sending user creation event...");
        try {
            notificationService.send(user);
        } catch (Exception e) {
            log.error("notification error", e);
        }
    }

    private void saveToOutBoxTable(User user) {
        log.info("Inserting into outbox table...");
        UserOutBox userOutBox = new UserOutBox();
        userOutBox.setUser(user);
        userOutBoxRepository.save(userOutBox);
    }

    public String update(int id, UserRequest request) {
        IMap<Integer, User> userCacheMap = getUserCacheMap();
        log.info("Retrieving from cache...");
        User user = userCacheMap.get(id);

        if (user == null){
            log.info("Cache is empty. Retrieving from database...");
            user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        }

        user = userRepository.save(UserMapper.map(request, user));
        log.info("User with id {} has been updated", id);

        userCacheMap.put(user.getId(), user);
        log.info("Inserted into cache");

        return "updated with id: "+user.getId();
    }

    @Transactional
    public String deleteUser(int id) {
        log.info("retrieving user from cache...");
        User user = getUserCacheMap().get(id);
        if (user == null) {
            log.info("the cache is empty");
            log.info("retrieving user from database...");
            user = userRepository.getReferenceById(id);
        }

        log.info("removing from outbox table...");
        userOutBoxRepository.deleteByUser(user.getId());

        userRepository.delete(user);
        log.info("User with id {} has been deleted", id);

        getUserCacheMap().delete(id);
        log.info("Removed from cache");

        return "deleted";
    }

    private IMap<Integer, User> getUserCacheMap(){
        return hazelcastInstance.getMap("userCache");
    }
}
