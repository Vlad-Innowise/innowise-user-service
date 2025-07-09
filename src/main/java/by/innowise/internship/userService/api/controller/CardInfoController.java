package by.innowise.internship.userService.api.controller;

import by.innowise.internship.userService.api.dto.cardInfo.CardInfoCreateDto;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoResponseDto;
import by.innowise.internship.userService.core.service.api.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{userId}/cards")
@RequiredArgsConstructor
@Slf4j
public class CardInfoController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardInfoResponseDto> create(@RequestBody @Valid CardInfoCreateDto dto,
                                                      @PathVariable Long userId) {
        log.info("Requested to create a card: {} for user with id: {}", dto, userId);
        CardInfoResponseDto responseDto = cardService.create(dto, userId);
        log.info("Sending a response to a client");
        return ResponseEntity.ok(responseDto);
    }

}
