package user.management.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, String> {
    private String password;
    private String rePassword;

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        password = constraintAnnotation.first();
        rePassword = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return password.equals(rePassword);
    }
}
