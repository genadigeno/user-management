package user.management.dto.http;

import jakarta.validation.constraints.NotEmpty;
import user.management.utils.FieldMatch;

@FieldMatch(first="password", second="rePassword", message="The password fields must match")
public class UserCreateRequest extends UserRequest {
    @NotEmpty(message = "rePassword should not be empty/null")
    private String rePassword;

    public String getRePassword() {
        return rePassword;
    }

    public void setRePassword(String rePassword) {
        this.rePassword = rePassword;
    }
}
