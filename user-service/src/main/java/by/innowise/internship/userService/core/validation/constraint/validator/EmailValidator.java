package by.innowise.internship.userService.core.validation.constraint.validator;

import by.innowise.internship.userService.core.service.api.UserService;
import by.innowise.internship.userService.core.validation.constraint.api.EmailUnique;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EmailValidator implements ConstraintValidator<EmailUnique, String> {

    private final UserService userService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return true;
        }
        return !userService.isEmailExists(email);
    }
}
