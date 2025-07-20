package by.innowise.internship.userService.core.validation.constraint.api;

import by.innowise.internship.userService.core.validation.constraint.validator.EmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The provided email must not be assigned to any existing user in the application.
 * {@code null} or {@code blank} elements are considered valid.
 */

@Constraint(validatedBy = EmailValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailUnique {

    String message() default "The email address already exists!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
