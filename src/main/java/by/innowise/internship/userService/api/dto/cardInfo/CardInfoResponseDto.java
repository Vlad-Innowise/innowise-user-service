package by.innowise.internship.userService.api.dto.cardInfo;

import by.innowise.internship.userService.core.platform.api.Versionable;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.UUID;

public record CardInfoResponseDto(

        UUID id,

        String number,

        String holder,

        @JsonProperty("expiration_date")
        LocalDate expirationDate,

        @JsonProperty("user_id")
        Long userId,

        Long version
) implements Versionable {

    @Override
    public Long getVersion() {
        return version;
    }
}
