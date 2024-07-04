package user.management.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import user.management.dto.PageDto;
import user.management.service.UserService;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @Tag(name = "Get the users", description = "GET users endpoint")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer JWT_TOKEN")
    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public PageDto getUsers(@RequestParam int page, @RequestParam int size){
        return userService.getUsers(page, size);
    }
}
