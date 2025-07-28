package by.innowise.internship.userService.core.exception;

import org.springframework.http.HttpStatus;

public class NotSupportedCacheException extends ApplicationException {

    public NotSupportedCacheException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

}
