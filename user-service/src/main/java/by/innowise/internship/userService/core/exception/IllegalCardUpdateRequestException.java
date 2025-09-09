package by.innowise.internship.userService.core.exception;

import by.innowise.common.library.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class IllegalCardUpdateRequestException extends ApplicationException {

    public IllegalCardUpdateRequestException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public IllegalCardUpdateRequestException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message, httpStatus, cause);
    }
}
