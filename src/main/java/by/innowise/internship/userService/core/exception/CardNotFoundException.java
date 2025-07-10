package by.innowise.internship.userService.core.exception;

import org.springframework.http.HttpStatus;

public class CardNotFoundException extends ApplicationException {


    public CardNotFoundException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public CardNotFoundException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message, httpStatus, cause);
    }

}
