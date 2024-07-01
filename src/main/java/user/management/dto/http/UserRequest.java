package user.management.dto.http;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserRequest {
//    @NotEmpty(message = "username should not be empty/null")
    @Length(min = 4, max = 500)
    private String username;
    @Email(message = "invalid email")
    @Length(min = 8, max = 500)
    private String email;
//    @NotEmpty(message = "password should not be empty/null")
    @Length(min = 8, max = 50, message = "password length must be between 8 and 50")
    private String password;
}
