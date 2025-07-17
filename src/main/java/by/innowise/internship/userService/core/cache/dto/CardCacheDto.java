package by.innowise.internship.userService.core.cache.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CardCacheDto implements Serializable {

    private UUID id;
    private String number;
    private String holder;
    private LocalDate expirationDate;
    private Long userId;
    private Long version;
}
