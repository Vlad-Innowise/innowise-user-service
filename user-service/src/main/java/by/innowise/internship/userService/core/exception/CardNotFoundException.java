package by.innowise.internship.userService.core.exception;

import by.innowise.common.library.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class CardNotFoundException extends ApplicationException {


    public CardNotFoundException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public CardNotFoundException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message, httpStatus, cause);
    }

}
