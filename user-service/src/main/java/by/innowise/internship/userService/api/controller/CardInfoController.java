package by.innowise.internship.userService.api.controller;

import by.innowise.internship.security.dto.UserHolder;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoCreateDto;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoResponseDto;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoUpdateDto;
import by.innowise.internship.userService.core.service.api.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CardInfoController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardInfoResponseDto> create(@RequestBody @Valid CardInfoCreateDto dto,
                                                      @AuthenticationPrincipal UserHolder userHolder) {
        Long authUserId = userHolder.crossServiceUserId();
        log.info("Requested to create a card: {} for user with auth user id: {}", dto, authUserId);
        CardInfoResponseDto responseDto = cardService.create(dto, authUserId);
        log.info("Sending a response to a client");
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardInfoResponseDto> getById(@AuthenticationPrincipal UserHolder userHolder,
                                                       @PathVariable UUID cardId) {
        Long authUserId = userHolder.crossServiceUserId();
        log.info("Requested to get a card with id: [{}] for auth user id: [{}]", cardId, authUserId);
        CardInfoResponseDto card = cardService.getById(cardId, authUserId);
        log.info("Sending a card response to a client {}", card);
        return ResponseEntity.ok(card);
    }

    @GetMapping
    public ResponseEntity<List<CardInfoResponseDto>> getAll(@AuthenticationPrincipal UserHolder userHolder,
                                                            @PageableDefault(sort = {"expirationDate"},
                                                                             direction = Sort.Direction.ASC)
                                                            Pageable pageable) {
        Long authUserId = userHolder.crossServiceUserId();
        log.info("Requested to get all cards for auth user id: [{}]", authUserId);
        List<CardInfoResponseDto> cards = cardService.getAll(authUserId, pageable);
        log.info("Sending all cards {} to a client for user id: [{}]", cards, authUserId);
        return ResponseEntity.ok(cards);
    }

    @PutMapping
    public ResponseEntity<CardInfoResponseDto> update(@AuthenticationPrincipal UserHolder userHolder,
                                                      @RequestBody @Valid CardInfoUpdateDto dto) {
        Long authUserId = userHolder.crossServiceUserId();
        log.info("Requested to update a card: {} for auth user id: {}", dto, authUserId);
        CardInfoResponseDto updated = cardService.update(dto, authUserId);
        log.info("Received updated response dto: {}. Sending response to a client", updated);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserHolder userHolder,
                                       @PathVariable UUID cardId) {
        Long authUserId = userHolder.crossServiceUserId();
        log.info("Requested to delete a card with id: [{}] for auth user id: [{}]", cardId, authUserId);
        cardService.delete(cardId, authUserId);
        return ResponseEntity.ok()
                             .build();
    }

}
