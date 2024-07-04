package user.management.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import user.management.dto.UserDto;
import user.management.dto.http.*;
import org.springframework.web.bind.annotation.*;
import user.management.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @Tag(name = "Get a user", description = "GET users endpoint")
    @ApiResponse(responseCode = "200", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDto.class)) })
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer JWT_TOKEN")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN') or (hasRole('USER') and #id==authentication.principal.id)")
    public UserDto user(@PathVariable int id){
        return userService.getUser(id);
    }

    //----------------------------------Register/Login------------------------------------
    @Tag(name = "Create a user", description = "Create user endpoint")
    @ApiResponse(responseCode = "200", content = { @Content(mediaType = "application/json",
            schema = @Schema(implementation = String.class)) })
    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "",
            content = @Content(schema = @Schema(anyOf = UserCreateRequest.class)))
    @PostMapping("/auth/register")
    public UserResponse register(@Validated @RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @Tag(name = "Create a user", description = "Create user endpoint")
    @ApiResponse(responseCode = "200", content = { @Content(mediaType = "application/json",
            schema = @Schema(implementation = String.class)) })
    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "",
            content = @Content(schema = @Schema(anyOf = UserCreateRequest.class)))
    @PostMapping("/auth/login")
    public LoginResponse login(@Validated @RequestBody LoginRequest request) {
        return userService.loginUser(request);
    }
    //-------------------------------------------------------------------------

    @Tag(name = "Update a user", description = "Update user endpoint")
    @ApiResponse(responseCode = "200", content = { @Content(mediaType = "application/json",
            schema = @Schema(implementation = String.class)) })
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer JWT_TOKEN")
    @PutMapping("/{id}")
    //@PreAuthorize("hasAnyRole('ADMIN') or (hasRole('USER') and #id==authentication.principal.id)")
    public UserResponse update(@PathVariable int id, @Validated @RequestBody UserRequest request){
        return userService.update(id, request);
    }

    @Tag(name = "Delete a user", description = "Delete user endpoint")
    @ApiResponse(responseCode = "200", content = { @Content(mediaType = "application/json",
            schema = @Schema(implementation = String.class)) })
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer JWT_TOKEN")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN') or (hasRole('USER') and #id==authentication.principal.id)")
    public UserResponse delete(@PathVariable int id){
        return userService.deleteUser(id);
    }
}
