package by.innowise.internship.userService.core.service.api;

import by.innowise.internship.userService.api.dto.cardInfo.CardInfoCreateDto;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoResponseDto;

public interface CardService {

    CardInfoResponseDto create(CardInfoCreateDto dto, Long userId);

    boolean cardNumberExists(String number);

}
