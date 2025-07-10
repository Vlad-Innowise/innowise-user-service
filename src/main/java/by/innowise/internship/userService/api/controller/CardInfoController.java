package by.innowise.internship.userService.api.controller;

import by.innowise.internship.userService.api.dto.cardInfo.CardInfoCreateDto;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoResponseDto;
import by.innowise.internship.userService.core.service.api.CardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/cards")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CardInfoController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardInfoResponseDto> create(@RequestBody @Valid CardInfoCreateDto dto,
                                                      @PathVariable @Positive Long userId) {
        log.info("Requested to create a card: {} for user with id: {}", dto, userId);
        CardInfoResponseDto responseDto = cardService.create(dto, userId);
        log.info("Sending a response to a client");
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardInfoResponseDto> getById(@PathVariable @Positive Long userId,
                                                       @PathVariable UUID cardId) {
        log.info("Requested to get a card with id: [{}] for user id: [{}]", cardId, userId);
        CardInfoResponseDto card = cardService.getById(cardId, userId);
        log.info("Sending a card response to a client {}", card);
        return ResponseEntity.ok(card);
    }

}
