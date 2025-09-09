package by.innowise.internship.userService.core.exception;

import by.innowise.common.library.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class NotSupportedCacheException extends ApplicationException {

    public NotSupportedCacheException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

}
