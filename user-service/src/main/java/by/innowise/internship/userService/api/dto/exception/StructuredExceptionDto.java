package by.innowise.internship.userService.api.dto.exception;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"field", "message"})
public record StructuredExceptionDto(
        String field,
        String message
) {
}
