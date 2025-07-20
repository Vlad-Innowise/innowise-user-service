package by.innowise.internship.userService.api.dto.exception;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"code", "errors"})
public record ComplexExceptionDto(

        Integer code,

        List<StructuredExceptionDto> errors
) {
}
