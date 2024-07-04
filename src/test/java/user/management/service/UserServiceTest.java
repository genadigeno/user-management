package user.management.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import user.management.dto.PageDto;
import user.management.dto.UserDto;
import user.management.dto.http.UserCreateRequest;
import user.management.dto.http.UserResponse;
import user.management.exception.UserAlreadyExistsException;
import user.management.exception.UserNotFoundException;
import user.management.model.Role;
import user.management.model.User;
import user.management.model.UserOutBox;
import user.management.model.UserRole;
import user.management.repository.RoleRepository;
import user.management.repository.UserOutBoxRepository;
import user.management.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    UserOutBoxRepository userOutBoxRepository;
    @Mock
    CacheService cacheService;
    @Mock
    NotificationService notificationService;

    int page = 1, size = 10;
    static User user;
    static UserCreateRequest createRequest;

    @BeforeAll
    public static void setup(){
        user = new User();
        user.setId(1);
        user.setPassword("psw12345678");
        user.setUsername("usr-535-ooo");
        user.setEmail("usr-535@gmail.com");

        createRequest = new UserCreateRequest();
        createRequest.setEmail("usr-535@gmail.com");
        createRequest.setUsername("usr-535-ooo");
        createRequest.setPassword("psw12345678");
        createRequest.setRePassword("psw12345678");
    }

    @Test
    void test_getUsers_success() {
        List<User> userList = List.of(user);
        Page<User> userPage = new PageImpl<>(userList, PageRequest.of(page, size), userList.size());

        when(userRepository.findAll(any(PageRequest.class)))
                .thenReturn(userPage);

        PageDto result =  assertDoesNotThrow(() -> userService.getUsers(page, size));

        assertEquals(userPage.getContent().size(), result.getData().size());
        assertEquals(userPage.getTotalElements(), result.getTotal());

        verify(userRepository, times(1))
                .findAll(any(PageRequest.class));
    }

    @Test
    void test_getUser_success() {
        when(cacheService.getFromCache(anyInt()))
                .thenReturn(user);

        UserDto userDto = assertDoesNotThrow(() -> userService.getUser(1));

        assertEquals(user.getUsername(), userDto.getUsername());
        verify(cacheService, times(1))
                .getFromCache(anyInt());
        verify(userRepository, times(0))
                .findById(anyInt());
    }

    @Test
    @DisplayName("test getUser with fail when cache is empty")
    void test_getUser_fail() {
        when(cacheService.getFromCache(anyInt()))
                .thenReturn(null);
        when(userRepository.findById(anyInt()))
                .thenThrow(new UserNotFoundException(1));

        assertThrows(UserNotFoundException.class, () -> userService.getUser(1));

        verify(cacheService, times(1))
                .getFromCache(anyInt());
        verify(userRepository, times(1))
                .findById(anyInt());
        verify(cacheService, times(0))
                .putIntoCache(anyInt(), any(User.class));
    }
    //----------------------------------------------------------------------------------------------
    @Test
    void test_createUser_success() {
        UserRole role = new UserRole();
        role.setRole(Role.ROLE_USER);

        when(userRepository.countByUsername(anyString()))
                .thenReturn(0L);
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        when(roleRepository.findByRole(any(Role.class)))
                .thenReturn(role);
        when(userOutBoxRepository.save(any(UserOutBox.class)))
                .thenReturn(new UserOutBox());
        doNothing()
                .when(notificationService).send(any(User.class));

        UserResponse response = assertDoesNotThrow(() -> userService.createUser(createRequest));

        assertTrue(response.getMessage().contains("created"));
        verify(userRepository, times(1)).countByUsername(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userOutBoxRepository, times(1)).save(any(UserOutBox.class));
        verify(notificationService, times(1)).send(any(User.class));
    }

    @Test
    void test_createUser_fail() {
        when(userRepository.countByUsername(anyString()))
                .thenReturn(1L);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(createRequest));

        verify(userRepository, times(1)).countByUsername(anyString());
        verify(userRepository, times(0)).save(any(User.class));
        verify(userOutBoxRepository, times(0)).save(any(UserOutBox.class));
        verify(notificationService, times(0)).send(any(User.class));
    }

    @Test
    void test_update_success() {
        when(cacheService.getFromCache(anyInt()))
                .thenReturn(null);
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        doNothing()
                .when(cacheService).putIntoCache(anyInt(), any(User.class));

        UserResponse response = assertDoesNotThrow(() -> userService.update(1, createRequest));
        assertTrue(response.getMessage().contains("updated"));
        verify(userRepository, times(1)).findById(anyInt());
        verify(userRepository, times(1)).save(any(User.class));
        verify(cacheService, times(1)).putIntoCache(anyInt(), any(User.class));

    }
    @Test
    void test_update_fail() {
        when(cacheService.getFromCache(anyInt()))
                .thenReturn(null);
        when(userRepository.findById(anyInt()))
                .thenThrow(new UserNotFoundException(1));

        assertThrows(UserNotFoundException.class, () -> userService.update(1, createRequest));

        verify(cacheService, times(1)).getFromCache(anyInt());
        verify(userRepository, times(1)).findById(anyInt());
        verify(userRepository, times(0)).save(any(User.class));
        verify(cacheService, times(0)).putIntoCache(anyInt(), any(User.class));
    }
}