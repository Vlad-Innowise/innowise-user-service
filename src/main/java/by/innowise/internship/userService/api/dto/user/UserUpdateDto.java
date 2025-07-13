package by.innowise.internship.userService.api.dto.user;

import by.innowise.internship.userService.core.util.api.Versionable;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserUpdateDto(

        @NotBlank(message = "User name can't be blank")
        @Size(max = 128, message = "User name can't exceed 128 symbols")
        String name,

        @NotBlank(message = "User surname can't be blank")
        @Size(max = 128, message = "User surname can't exceed 128 symbols")
        String surname,

        @Past(message = "User birth date must be in past")
        @JsonProperty("birth_date")
        LocalDate birthDate,

        @NotBlank(message = "User email address can't be null")
        @Email(message = "Invalid email address")
        @Size(max = 255, message = "User email can't exceed 255 symbols")
        String email,

        @NotNull(message = "User version can't be null")
        @PositiveOrZero(message = "User version should be equal positive or zero")
        Long version

) implements Versionable {

    @Override
    public Long getVersion() {
        return version;
    }
}
