package user.management.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import user.management.dto.PageDto;
import user.management.dto.UserDto;
import user.management.dto.http.UserCreateRequest;
import user.management.dto.http.UserRequest;
import user.management.exception.UserAlreadyExistsException;
import user.management.exception.UserNotFoundException;
import user.management.model.User;
import user.management.repository.UserRepository;
import user.management.utils.UserMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final HazelcastInstance hazelcastInstance;

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

        return "created with id: "+saved.getId();
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

    public String deleteUser(int id) {
        userRepository.deleteById(id);
        log.info("User with id {} has been deleted", id);

        getUserCacheMap().delete(id);
        log.info("Removed from cache");

        return "deleted";
    }

    private IMap<Integer, User> getUserCacheMap(){
        return hazelcastInstance.getMap("userCache");
    }
}
