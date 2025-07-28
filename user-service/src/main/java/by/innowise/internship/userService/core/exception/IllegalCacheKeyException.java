package by.innowise.internship.userService.core.exception;

import org.springframework.http.HttpStatus;

public class IllegalCacheKeyException extends ApplicationException {

    public IllegalCacheKeyException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
