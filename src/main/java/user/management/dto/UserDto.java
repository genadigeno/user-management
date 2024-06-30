package user.management.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDto {
    private String username;
    private String email;
    private boolean enabled;
    private LocalDateTime created;

}
