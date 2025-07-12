package by.innowise.internship.userService.core.exception;

import org.springframework.http.HttpStatus;

public class UpdateDtoVersionOutdatedException extends ApplicationException {

    public UpdateDtoVersionOutdatedException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

}
