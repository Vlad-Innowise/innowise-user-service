package by.innowise.internship.userService.core.service.api;

import by.innowise.internship.userService.api.dto.cardInfo.CardInfoCreateDto;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoResponseDto;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoUpdateDto;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CardService {

    CardInfoResponseDto create(CardInfoCreateDto dto, Long userId);

    boolean cardNumberExists(String number);

    CardInfoResponseDto getById(UUID cardId, @Positive Long userId);

    List<CardInfoResponseDto> getAll(Long userId, Pageable pageable);

    CardInfoResponseDto update(CardInfoUpdateDto dto, Long userId);

    void delete(UUID cardId, Long userId);

    List<CardInfoResponseDto> getAllByIds(List<UUID> ids);
}
