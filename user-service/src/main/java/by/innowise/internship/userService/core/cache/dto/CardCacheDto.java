package by.innowise.internship.userService.core.cache.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardCacheDto implements Serializable {

    private UUID id;
    private String number;
    private String holder;
    private LocalDate expirationDate;
    private Long userId;
    private Long version;
}
