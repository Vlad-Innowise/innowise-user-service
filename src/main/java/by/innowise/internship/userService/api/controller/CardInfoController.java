package by.innowise.internship.userService.api.controller;

import by.innowise.internship.userService.api.dto.cardInfo.CardInfoCreateDto;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoResponseDto;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoUpdateDto;
import by.innowise.internship.userService.core.service.api.CardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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

    @GetMapping
    public ResponseEntity<List<CardInfoResponseDto>> getAll(@PathVariable @Positive Long userId,
                                                            @PageableDefault(sort = {"expirationDate"},
                                                                             direction = Sort.Direction.ASC)
                                                            Pageable pageable) {
        log.info("Requested to get all cards for user id: [{}]", userId);
        List<CardInfoResponseDto> cards = cardService.getAll(userId, pageable);
        log.info("Sending all cards {} to a client for user id: [{}]", cards, userId);
        return ResponseEntity.ok(cards);
    }

    @PutMapping
    public ResponseEntity<CardInfoResponseDto> update(@PathVariable @Positive Long userId,
                                                      @RequestBody @Valid CardInfoUpdateDto dto) {
        log.info("Requested to update a card: {} for user id: {}", dto, userId);
        CardInfoResponseDto updated = cardService.update(dto, userId);
        log.info("Received updated response dto: {}. Sending response to a client", updated);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<HttpStatus> delete(@PathVariable @Positive Long userId,
                                             @PathVariable UUID cardId) {
        log.info("Requested to delete a card with id: [{}] for user id: [{}]", cardId, userId);
        cardService.delete(cardId, userId);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
