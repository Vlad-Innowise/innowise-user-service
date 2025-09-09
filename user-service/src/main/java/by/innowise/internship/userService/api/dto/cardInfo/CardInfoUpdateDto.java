package by.innowise.internship.userService.api.dto.cardInfo;

import by.innowise.common.library.util.Versionable;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record CardInfoUpdateDto(

        @NotNull(message = "Card ID must not be null")
        UUID id,

        @NotBlank(message = "Card number can't be blank")
        @Pattern(regexp = "\\d{16}", message = "Invalid card number - must be 16 digits only")
        String number,

        @NotBlank(message = "Card holder name can't be blank")
        @Size(max = 64, message = "Card holder name can't exceed 64 symbols")
        String holder,

        @NotNull(message = "Card expiration date can't be null!")
        @Future(message = "Card expiration date must be in future!")
        @JsonProperty("expiration_date")
        LocalDate expirationDate,

        @NotNull(message = "Version id must not be null")
        @PositiveOrZero(message = "Card info version can't be negative!")
        Long version
) implements Versionable {

    @Override
    public Long getVersion() {
        return version;
    }
}
