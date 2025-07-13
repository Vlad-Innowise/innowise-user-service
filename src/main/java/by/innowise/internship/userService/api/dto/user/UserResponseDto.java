package by.innowise.internship.userService.api.dto.user;

import by.innowise.internship.userService.api.dto.cardInfo.CardInfoResponseDto;
import by.innowise.internship.userService.core.util.api.Versionable;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record UserResponseDto(

        Long id,

        String name,

        String surname,

        @JsonProperty("birth_date")
        LocalDate birthDate,

        String email,

        Long version,

        List<CardInfoResponseDto> cards

) implements Versionable {

    @Override
    public Long getVersion() {
        return version;
    }
}
