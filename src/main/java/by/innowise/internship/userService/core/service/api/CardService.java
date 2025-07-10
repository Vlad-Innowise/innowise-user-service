package by.innowise.internship.userService.core.service.api;

import by.innowise.internship.userService.api.dto.cardInfo.CardInfoCreateDto;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoResponseDto;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public interface CardService {

    CardInfoResponseDto create(CardInfoCreateDto dto, Long userId);

    boolean cardNumberExists(String number);

    CardInfoResponseDto getById(UUID cardId, @Positive Long userId);
}
