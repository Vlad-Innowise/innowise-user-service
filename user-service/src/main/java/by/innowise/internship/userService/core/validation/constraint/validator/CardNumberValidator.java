package by.innowise.internship.userService.core.validation.constraint.validator;

import by.innowise.internship.userService.core.service.api.CardService;
import by.innowise.internship.userService.core.validation.constraint.api.CardNumberUnique;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardNumberValidator implements ConstraintValidator<CardNumberUnique, String> {

    private final CardService cardService;

    @Override
    public boolean isValid(String number, ConstraintValidatorContext context) {
        if (number == null || number.isBlank()) {
            return true;
        }
        return !cardService.cardNumberExists(number);
    }

}
