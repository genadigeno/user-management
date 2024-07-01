package user.management.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import user.management.dto.PageDto;
import user.management.dto.UserDto;
import user.management.dto.http.UserCreateRequest;
import user.management.dto.http.UserRequest;
import org.springframework.web.bind.annotation.*;
import user.management.service.UserService;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController { //TODO: Add JWT Token description
    private final UserService userService;

    @Tag(name = "Get the users", description = "GET users endpoint")
    @GetMapping
    public PageDto users(@RequestParam(defaultValue="10") int size, @RequestParam(defaultValue="0") int page){
        return userService.getUsers(size, page);
    }

    @Tag(name = "Get a user", description = "GET users endpoint")
    @ApiResponse(responseCode = "200", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDto.class)) })
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    @GetMapping("/{id}")
    public UserDto user(@PathVariable int id){
        return userService.getUser(id);
    }

    @Tag(name = "Create a user", description = "Create user endpoint")
    @ApiResponse(responseCode = "200", content = { @Content(mediaType = "application/json",
            schema = @Schema(implementation = String.class)) })
    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "",
            content = @Content(schema = @Schema(anyOf = UserCreateRequest.class)))
    @PostMapping
    public String create(@Validated @RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @Tag(name = "Update a user", description = "Update user endpoint")
    @ApiResponse(responseCode = "200", content = { @Content(mediaType = "application/json",
            schema = @Schema(implementation = String.class)) })
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    @PutMapping("/{id}")
    public String update(@PathVariable int id, @Validated @RequestBody UserRequest request){
        return userService.update(id, request);
    }

    @Tag(name = "Delete a user", description = "Delete user endpoint")
    @ApiResponse(responseCode = "200", content = { @Content(mediaType = "application/json",
            schema = @Schema(implementation = String.class)) })
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    @DeleteMapping("/{id}")
    public String delete(@PathVariable int id){
        return userService.deleteUser(id);
    }
}
