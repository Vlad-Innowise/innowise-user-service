package by.innowise.internship.userService.api.dto.exception;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"code", "message"})
public record SimpleExceptionDto(
        Integer code,
        String message
) {
}
