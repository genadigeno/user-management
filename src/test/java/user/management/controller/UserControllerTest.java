package user.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.github.dockerjava.core.MediaType;
import user.management.dto.PageDto;
import user.management.dto.UserDto;
import user.management.dto.http.UserCreateRequest;
import user.management.dto.http.UserRequest;
import user.management.dto.http.UserResponse;
import user.management.exception.UserNotFoundException;
import user.management.service.JwtService;
import user.management.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @MockBean
    JwtService jwtService;

    static UserCreateRequest createRequest;

    @BeforeAll
    static void setup(){
        createRequest = new UserCreateRequest();
        createRequest.setEmail("usr-535@gmail.com");
        createRequest.setUsername("usr-535-ooo");
        createRequest.setPassword("psw12345678");
        createRequest.setRePassword("psw12345678");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void test_users_with_success() throws Exception {
        when(userService.getUsers(anyInt(), anyInt()))
                .thenReturn(PageDto.builder().build());

        mockMvc.perform(get("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON.getMediaType()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void test_users_with_fail() throws Exception {
        when(userService.getUsers(anyInt(), anyInt()))
                .thenThrow(new UserNotFoundException(1));

        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON.getMediaType()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user")
    void test_user_with_success() throws Exception {
        when(userService.getUser(anyInt()))
                .thenReturn(UserDto.builder().build());

        mockMvc.perform(get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON.getMediaType()))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "user")
    void test_user_with_fail() throws Exception {
        when(userService.getUser(anyInt()))
                .thenThrow(new UserNotFoundException(1));

        mockMvc.perform(get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON.getMediaType()))
                .andExpect(status().isBadRequest());
    }

    /*@Test
    void test_create_with_success() throws Exception {
        when(userService.createUser(any(UserCreateRequest.class)))
                .thenReturn(UserResponse.builder()
                        .message("user created")
                        .build());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON.getMediaType())
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());
    }
    @Test
    void test_create_with_fail() throws Exception {
        when(userService.createUser(any(UserCreateRequest.class)))
                .thenThrow(new UserAlreadyExistsException());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON.getMediaType())
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }*/

    @Test
    @WithMockUser(username = "user")
    void test_update_with_success() throws Exception {
        /*User user = new User();
        user.setId(1); // Set the ID to match the path variable
        user.setUsername("user");
        user.setPassword("password");
        UserRole role = new UserRole();
        role.setRole(Role.ROLE_USER);
        user.setUserRoles(List.of(role)); // Assuming you have a Role entity

        // Mock the authentication
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);*/

        when(userService.update(anyInt(), any(UserRequest.class)))
                .thenReturn(UserResponse.builder().build());

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON.getMediaType())
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "user")
    void test_update_with_fail() throws Exception {
        when(userService.update(anyInt(), any(UserRequest.class)))
                .thenThrow(new UserNotFoundException(1));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON.getMediaType())
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user")
    void test_delete_with_success() throws Exception {
        when(userService.deleteUser(anyInt()))
                .thenReturn(UserResponse.builder().build());

        mockMvc.perform(delete("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON.getMediaType()))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "user")
    void test_delete_with_fail() throws Exception {
        when(userService.deleteUser(anyInt()))
                .thenThrow(new UserNotFoundException(1));

        mockMvc.perform(delete("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON.getMediaType()))
                .andExpect(status().isBadRequest());
    }
}