package by.innowise.internship.userService.core.cache.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
public class UserCacheDto implements Serializable {

    Long id;
    String name;
    String surname;
    LocalDate birthDate;
    String email;
    Long version;
    List<CardCacheDto> cards;

}
