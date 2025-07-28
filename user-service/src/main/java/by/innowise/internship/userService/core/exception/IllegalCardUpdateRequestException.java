package by.innowise.internship.userService.core.exception;

import org.springframework.http.HttpStatus;

public class IllegalCardUpdateRequestException extends ApplicationException {

    public IllegalCardUpdateRequestException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public IllegalCardUpdateRequestException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message, httpStatus, cause);
    }
}
