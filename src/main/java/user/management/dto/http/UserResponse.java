package user.management.dto.http;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String message;
    private int userId;
}
