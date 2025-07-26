package by.innowise.internship.userService.core.validation.constraint.api;

import by.innowise.internship.userService.core.validation.constraint.validator.CardNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The provided card number must not be assigned to any existing user in the application.
 * {@code null} or {@code blank} numbers are considered valid.
 */

@Constraint(validatedBy = CardNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CardNumberUnique {

    String message() default "The card number is already in use!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
