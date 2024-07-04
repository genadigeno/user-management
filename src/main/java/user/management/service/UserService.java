package user.management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import user.management.dto.PageDto;
import user.management.dto.UserDto;
import user.management.dto.http.*;
import user.management.exception.UserAlreadyExistsException;
import user.management.exception.UserNotFoundException;
import user.management.model.Role;
import user.management.model.User;
import user.management.model.UserOutBox;
import user.management.repository.RoleRepository;
import user.management.repository.UserOutBoxRepository;
import user.management.repository.UserRepository;
import user.management.utils.UserMapper;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserOutBoxRepository userOutBoxRepository;

    private final CacheService cacheService;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public PageDto getUsers(int page, int size) {
        log.info("Get users request for page {} with size {}", page, size);
        Page<User> userPage = userRepository.findAll(PageRequest.of(page, size));
        Page<UserDto> users = userPage.map(UserMapper::map);
        return PageDto.builder()
                .data(users.getContent())
                .total(users.getTotalElements())
                .build();
    }

    public UserDto getUser(int id) {
        log.info("Get user request for id {}", id);

        log.info("Retrieving from cache...");
        User user = cacheService.getFromCache(id);
        if (user == null){
            log.info("Cache is empty. Retrieving from database...");
            user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
            cacheService.putIntoCache(user.getId(), user);
            log.info("Inserted into cache.");
        }

        return UserMapper.map(user);
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        long count = userRepository.countByUsername(request.getUsername());
        if (count > 0) {
            log.warn("User with username {} already exists", request.getUsername());
            throw new UserAlreadyExistsException();
        }

        User user = UserMapper.map(request);
        user.setPassword(passwordEncoder.encode(request.getRePassword()));
        user.setUserRoles(List.of(roleRepository.findByRole(Role.ROLE_USER)));
        user = userRepository.save(user);
        log.info("User with id has been created");

        //insert into outbox table
        saveToOutBoxTable(user);

        sendEvent(user);//different transaction scope

        return UserResponse.builder()
                .message("user created")
                .userId(user.getId())
                .build();
    }

    public LoginResponse loginUser(final LoginRequest request) {
        Optional<User> user = userRepository.findByUsername(request.getUsername());
        String token = jwtService.generateToken(user.orElseThrow(() -> new UserNotFoundException(request.getUsername())));
        return LoginResponse.builder()
                .message("success")
                .token(token)
                .build();
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

    public UserResponse update(int id, UserRequest request) {
        log.info("Retrieving from cache...");
        User user = cacheService.getFromCache(id);

        if (user == null){
            log.info("Cache is empty. Retrieving from database...");
            user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        }

        user = userRepository.save(UserMapper.map(request, user));
        log.info("User with id {} has been updated", id);

        cacheService.putIntoCache(user.getId(), user);
        log.info("Inserted into cache");

        return UserResponse.builder()
                .message("user updated")
                .userId(user.getId())
                .build();
    }

    @Transactional
    public UserResponse deleteUser(int id) {
        log.info("retrieving user from cache...");
        User user = cacheService.getFromCache(id);
        if (user == null) {
            log.info("the cache is empty");
            log.info("retrieving user from database...");
            user = userRepository.getReferenceById(id);
        }

        log.info("removing from outbox table...");
        userOutBoxRepository.deleteByUser(user.getId());

        userRepository.delete(user);
        log.info("User with id {} has been deleted", id);

        cacheService.deleteFromCache(id);
        log.info("Removed from cache");

        return UserResponse.builder()
                .message("deleted")
                .userId(id)
                .build();
    }
}
