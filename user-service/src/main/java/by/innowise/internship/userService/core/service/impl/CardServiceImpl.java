package by.innowise.internship.userService.core.service.impl;

import by.innowise.common.library.util.ValidationUtil;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoCreateDto;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoResponseDto;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoUpdateDto;
import by.innowise.internship.userService.core.cache.CacheUtil;
import by.innowise.internship.userService.core.cache.CardInfoCacheService;
import by.innowise.internship.userService.core.cache.UserCacheInvalidator;
import by.innowise.internship.userService.core.cache.dto.CardCacheDto;
import by.innowise.internship.userService.core.cache.supportedCaches.CardCache;
import by.innowise.internship.userService.core.exception.CardNotFoundException;
import by.innowise.internship.userService.core.exception.IllegalCardUpdateRequestException;
import by.innowise.internship.userService.core.mapper.CardInfoMapper;
import by.innowise.internship.userService.core.repository.CardInfoRepository;
import by.innowise.internship.userService.core.repository.entity.CardInfo;
import by.innowise.internship.userService.core.repository.entity.User;
import by.innowise.internship.userService.core.service.api.CardService;
import by.innowise.internship.userService.core.service.api.InternalUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final CardInfoRepository cardRepository;
    private final CardInfoMapper mapper;
    private final InternalUserService internalUserService;
    private final CardInfoCacheService cardInfoCacheService;
    private final CacheUtil cacheUtil;
    private final UserCacheInvalidator userCacheInvalidator;

    @Transactional
    @Override
    public CardInfoResponseDto create(CardInfoCreateDto dto, Long userId) {
        log.info("Invoking internal user service to get a user by userId: [{}]", userId);
        User user = internalUserService.getUserById(userId);

        log.info("Mapping cardInfoCreateDto:{} and user:{} to card entity", dto, user);
        CardInfo toSave = mapper.toEntity(dto, user);

        log.info("Invoking card repository to save card: {}", toSave);
        CardInfo saved = cardRepository.saveAndFlush(toSave);
        log.info("Get pre-saved card entity from repository: {}", saved);

        updateCache(saved);

        invalidatingUserCache(userId);

        log.info("Map user entity: {} to dto", saved);
        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean cardNumberExists(String number) {
        log.info("Checking if card number: {} already exists in the database", number);
        return cardRepository.existsByNumber(number);
    }

    @Transactional
    @Override
    public CardInfoResponseDto getById(UUID cardId, Long userId) {
        String cacheKey = cacheUtil.composeKey("id", cardId);
        log.info("Trying to retrieve a card from cache by key: {}", cacheKey);
        return cardInfoCacheService
                .readFromCache(CardCache.BY_ID, cacheKey)
                .map(cached -> {
                    log.info("Retrieved a card from the cache: {}", cached);
                    return mapper.toDto(cached);
                })
                .orElseGet(() -> {
                    log.info("Not found in cache by key: {}, go to DB", cacheKey);
                    CardInfo found = getCardByCardIdAndUserId(cardId, userId);
                    updateCache(found);
                    return mapper.toDto(found);
                });
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
            ValidationUtil.checkIfDtoVersionIsOutdated(foundById.getVersion(), dto);

            log.info("Check if provided card number [{}] exists in the system and matches to requested card id [{}]",
                     dto.number(), dto.id());
            cardRepository.findByNumber(dto.number())
                          .ifPresent(foundByNumber ->
                                             checkIfFoundCardByNumberMatchRequestedCardId(foundByNumber, foundById)
                          );
            updated = mapper.updateEntity(dto, foundById);
            log.info("Updated entity {} to save", updated);
            cardRepository.saveAndFlush(updated);

            updateCache(updated);
            invalidatingUserCache(userId);
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
        String cacheKey = cacheUtil.composeKey("id", cardId);
        log.info("Invoking cache service to remove a cache for a key: {}", cacheKey);
        cardInfoCacheService.removeFromCache(CardCache.BY_ID, cacheKey);
        invalidatingUserCache(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CardInfoResponseDto> getAllByIds(List<UUID> ids) {
        Set<UUID> idsToFind = new HashSet<>(ids);
        log.info("Invoking card repository for ids: {}", idsToFind);
        List<CardInfo> foundCards = cardRepository.findAllByIdIn(idsToFind);
        log.info("Retrieved cards list: {}", foundCards);

        if (foundCards.size() != idsToFind.size()) {
            foundCards.forEach(u -> idsToFind.remove(u.getId()));
            log.info("Haven't found cards with ids: {}", idsToFind);
        }

        return foundCards.stream()
                         .map(mapper::toDto)
                         .toList();
    }

    private CardInfo getCardByCardIdAndUserId(UUID cardId, Long userId) {
        log.info("Invoking card repository to find a card with id: [{}] for userId: [{}]", cardId, userId);
        CardInfo found = cardRepository.findByIdAndUserId(cardId, userId).orElseThrow(
                () -> new CardNotFoundException(
                        String.format("Haven't found a card with id: [%s] for user: [%s]", cardId, userId),
                        HttpStatus.NOT_FOUND));
        log.info("Retrieved a card from from DB: {}", found);
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

    private void updateCache(CardInfo entity) {
        CardCacheDto cacheDto = mapper.toRedisDto(entity);
        String cacheKey = cacheUtil.composeKey("id", cacheDto.getId());
        log.info("Putting value: {} into cache: [{}]", cacheDto, CardCache.BY_ID.getCacheName());
        cardInfoCacheService.updateCache(CardCache.BY_ID, cacheKey, cacheDto);
    }


    private void invalidatingUserCache(Long userId) {
        log.info("Invalidating a user cache for user: [{}]", userId);
        userCacheInvalidator.invalidate(userId);
    }
}
