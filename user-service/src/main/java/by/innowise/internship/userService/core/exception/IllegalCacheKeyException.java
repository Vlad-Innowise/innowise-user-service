package by.innowise.internship.userService.core.exception;

import by.innowise.common.library.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class IllegalCacheKeyException extends ApplicationException {

    public IllegalCacheKeyException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
