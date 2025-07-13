package by.innowise.internship.userService.core.service.impl;

import by.innowise.internship.userService.api.dto.cardInfo.CardInfoCreateDto;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoResponseDto;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoUpdateDto;
import by.innowise.internship.userService.core.exception.CardNotFoundException;
import by.innowise.internship.userService.core.exception.IllegalCardUpdateRequestException;
import by.innowise.internship.userService.core.mapper.CardInfoMapper;
import by.innowise.internship.userService.core.repository.CardInfoRepository;
import by.innowise.internship.userService.core.repository.entity.CardInfo;
import by.innowise.internship.userService.core.service.api.CardService;
import by.innowise.internship.userService.core.util.validation.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final CardInfoRepository cardRepository;
    private final CardInfoMapper mapper;
    private final ValidationUtil validationUtil;

    @Transactional
    @Override
    public CardInfoResponseDto create(CardInfoCreateDto dto, Long userId) {
        CardInfo saved = cardRepository.saveAndFlush(mapper.toEntity(dto, userId));
        log.info("Saved a card info: {}", saved);
        CardInfoResponseDto responseDto = mapper.toDto(saved);
        log.info("Returning a response dto: {}", responseDto);
        return responseDto;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean cardNumberExists(String number) {
        log.info("Checking if card number: {} already exists in the database", number);
        return cardRepository.existsByNumber(number);
    }

    @Transactional(readOnly = true)
    @Override
    public CardInfoResponseDto getById(UUID cardId, Long userId) {
        CardInfo found = getCardByCardIdAndUserId(cardId, userId);
        return mapper.toDto(found);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CardInfoResponseDto> getAll(Long userId, Pageable pageable) {
        log.info("Invoking card repository to retrieve all cards for user with id: [{}]", userId);
        Page<CardInfo> cards = cardRepository.findAllByUserId(userId, pageable);
        log.info("Retrieved cards page {} out of {} for the userId: [{}]: {}", cards.getNumber(), cards.getTotalPages(),
                 userId, cards.getContent());
        return cards.stream()
                    .map(mapper::toDto)
                    .toList();
    }

    @Transactional
    @Override
    public CardInfoResponseDto update(CardInfoUpdateDto dto, Long userId) {

        CardInfo foundById = getCardByCardIdAndUserId(dto.id(), userId);

        CardInfo updated;

        if (hasAnyFieldChanged(dto, foundById)) {
            log.info("Check if the provided dto version is not outdated. Dto: [{}] Entity: [{}]", dto, foundById);
            validationUtil.checkIfDtoVersionIsOutdated(foundById.getVersion(), dto);

            log.info("Check if provided card number [{}] exists in the system and matches to requested card id [{}]",
                     dto.number(), dto.id());
            cardRepository.findByNumber(dto.number())
                          .ifPresent(foundByNumber ->
                                             checkIfFoundCardByNumberMatchRequestedCardId(foundByNumber, foundById)
                          );


            updated = mapper.updateEntity(dto, foundById);
            log.info("Updated entity {} to save", updated);
            cardRepository.saveAndFlush(updated);
        } else {
            log.info("Non of the fields in the dto {} have changed any of the fields in the entity {}", dto, foundById);
            updated = foundById;
        }
        return mapper.toDto(updated);
    }

    @Transactional
    @Override
    public void delete(UUID cardId, Long userId) {
        CardInfo found = getCardByCardIdAndUserId(cardId, userId);
        log.info("Invoking card repository to delete a card: [{}]", found);
        cardRepository.delete(found);
    }

    private CardInfo getCardByCardIdAndUserId(UUID cardId, Long userId) {
        log.info("Invoking card repository to find a card with id: [{}] for userId: [{}]", cardId, userId);
        CardInfo found = cardRepository.findByIdAndUserId(cardId, userId).orElseThrow(
                () -> new CardNotFoundException(
                        String.format("Haven't found a card with id: [%s] for user: [%s]", cardId, userId),
                        HttpStatus.NOT_FOUND));
        log.info("Retrieved a card {}", found);
        return found;
    }

    private boolean hasAnyFieldChanged(CardInfoUpdateDto d, CardInfo e) {
        return !(Objects.equals(d.holder(), e.getHolder()) &&
                Objects.equals(d.number(), e.getNumber()) &&
                Objects.equals(d.expirationDate(), e.getExpirationDate())
        );
    }

    private void checkIfFoundCardByNumberMatchRequestedCardId(CardInfo foundByNumber, CardInfo foundById) {
        if (!foundByNumber.getId().equals(foundById.getId())) {
            throw new IllegalCardUpdateRequestException(
                    String.format(
                            "Can't update the card number. The card with number [%s] already exists and has card id [%s]," +
                                    "which doesn't match the requested card id for update [%s] for user [%s]",
                            foundByNumber.getNumber(), foundByNumber.getId(), foundById.getId(),
                            foundById.getUser().getId()),
                    HttpStatus.BAD_REQUEST);
        }
    }

}
