package by.innowise.internship.userService.core.cache.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCacheDto implements Serializable {

    Long id;
    String name;
    String surname;
    LocalDate birthDate;
    String email;
    Long version;
    List<CardCacheDto> cards;

}
