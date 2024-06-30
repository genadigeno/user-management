package user.management.dto.http;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import user.management.utils.PasswordMatch;

@EqualsAndHashCode(callSuper = true)
@Data
@PasswordMatch(first="password", second="rePassword", message="The password fields must match")
public class UserCreateRequest extends UserRequest {
    @NotEmpty(message = "rePassword should not be empty/null")
    private String rePassword;
}
