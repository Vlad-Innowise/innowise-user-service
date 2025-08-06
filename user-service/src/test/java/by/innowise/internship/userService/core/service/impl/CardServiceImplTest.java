package by.innowise.internship.userService.core.service.impl;

import by.innowise.internship.userService.TestUtil;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoCreateDto;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoResponseDto;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoUpdateDto;
import by.innowise.internship.userService.core.cache.CacheUtil;
import by.innowise.internship.userService.core.cache.CardInfoCacheService;
import by.innowise.internship.userService.core.cache.UserCacheInvalidator;
import by.innowise.internship.userService.core.cache.dto.CardCacheDto;
import by.innowise.internship.userService.core.cache.supportedCaches.CacheType;
import by.innowise.internship.userService.core.cache.supportedCaches.CardCache;
import by.innowise.internship.userService.core.exception.CardNotFoundException;
import by.innowise.internship.userService.core.exception.IllegalCardUpdateRequestException;
import by.innowise.internship.userService.core.exception.UpdateDtoVersionOutdatedException;
import by.innowise.internship.userService.core.exception.UserNotFoundException;
import by.innowise.internship.userService.core.mapper.CardInfoMapper;
import by.innowise.internship.userService.core.repository.CardInfoRepository;
import by.innowise.internship.userService.core.repository.entity.CardInfo;
import by.innowise.internship.userService.core.repository.entity.User;
import by.innowise.internship.userService.core.service.api.InternalUserService;
import by.innowise.internship.userService.core.util.validation.ValidationUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final int MAX_PAGE_SIZE = 50;
    private static final int FIRST_PAGE = 0;
    private static User USER_WITH_SEVERAL_CARDS;
    private static CardInfo CARD_TO_MANIPULATE;
    @Mock
    private CardInfoRepository cardRepository;
    @Mock
    private CardInfoMapper mapper;
    @Mock
    private InternalUserService internalUserService;
    @Spy
    private ValidationUtil validationUtil;
    @Mock
    private CardInfoCacheService cardInfoCacheService;
    @Spy
    private CacheUtil cacheUtil;
    @Mock
    private UserCacheInvalidator userCacheInvalidator;
    @InjectMocks
    private CardServiceImpl cardService;

    private CardInfo testCard;
    private User testUser;

    @BeforeAll
    static void init() {
        System.out.println();
        CARD_TO_MANIPULATE = TestUtil.getCard("6666777788889999", "Test_2 With_Several_Cards",
                                              LocalDate.of(2029, 6, 30));

        List<CardInfo> cards = List.of(
                TestUtil.getCard("4444555566667777", "Test_2 With_Several_Cards", LocalDate.of(2030, 10, 31)),
                CARD_TO_MANIPULATE);

        USER_WITH_SEVERAL_CARDS = TestUtil.getUser(2L, "Test_2", "With_Several_Cards", LocalDate.of(1990, 12, 13),
                                                   "several_cards@email.com");

        cards.forEach(c -> {
            c.setUser(USER_WITH_SEVERAL_CARDS);
            USER_WITH_SEVERAL_CARDS.addCard(c);
        });

    }

    static Stream<Arguments> checkIfCardNumberExists() {
        return Stream.of(
                Arguments.of(CARD_TO_MANIPULATE.getNumber(), true),
                Arguments.of("0000000000001111", false)
        );
    }

    @BeforeEach
    void prepareTest() {
        this.testUser = TestUtil.deepCopyUser(USER_WITH_SEVERAL_CARDS);
        this.testCard = TestUtil.copyCard(CARD_TO_MANIPULATE);
        this.testCard.setUser(testUser);
    }

    @ParameterizedTest(name = "[{index}]: Check card number [{0}] awaiting result: {1}")
    @MethodSource("checkIfCardNumberExists")
    void cardNumberExists(String cardNumber, boolean expectedResult) {

        when(cardRepository.existsByNumber(cardNumber)).thenReturn(expectedResult);

        assertEquals(expectedResult, cardService.cardNumberExists(cardNumber));
        verify(cardRepository, times(1)).existsByNumber(anyString());
    }

    private List<UUID> generateMissingCardIds(int amount) {
        return Stream.generate(UUID::randomUUID)
                     .filter(this::isUuidNotAssigned)
                     .limit(amount)
                     .collect(Collectors.toCollection(ArrayList::new));
    }

    private boolean isUuidNotAssigned(UUID uuid) {
        return testUser.getCards().stream()
                       .map(CardInfo::getId)
                       .noneMatch(id -> Objects.equals(id, uuid));
    }

    @DisplayName("testing create() method")
    @Nested
    class create {

        @Test
        void createHappyPass() {

            long userId = testUser.getId();
            CardInfoCreateDto createDto = new CardInfoCreateDto("88885555444433331111", "Test_2 With_Several_Cards",
                                                                LocalDate.of(2027, 11, 30));
            CardInfo created = TestUtil.getCard(createDto.number(), createDto.holder(), createDto.expirationDate());
            created.setUser(testUser);
            testUser.getCards().add(created);

            when(internalUserService.getUserById(userId)).thenReturn(testUser);

            CardInfo toSave = TestUtil.copyCard(created);
            toSave.setId(null);
            toSave.setCreatedAt(null);
            toSave.setUpdatedAt(null);
            toSave.setVersion(null);
            when(mapper.toEntity(createDto, testUser)).thenReturn(toSave);

            when(cardRepository.saveAndFlush(toSave)).thenReturn(created);
            when(mapper.toRedisDto(created)).thenReturn(TestUtil.mapToCardRedisDto(created));
            doNothing().when(cardInfoCacheService).updateCache(any(CacheType.class), anyString(),
                                                               any(CardCacheDto.class));
            doNothing().when(userCacheInvalidator).invalidate(userId);

            CardInfoResponseDto expectedResult = TestUtil.mapToCardInfoResponseDto(created);
            when(mapper.toDto(created)).thenReturn(expectedResult);

            CardInfoResponseDto actualResult = cardService.create(createDto, userId);
            assertEquals(expectedResult, actualResult);
            verify(internalUserService, times(1)).getUserById(anyLong());
            verify(mapper, times(1)).toEntity(any(CardInfoCreateDto.class), any(User.class));
            verify(cardRepository, times(1)).saveAndFlush(any(CardInfo.class));
            verify(cacheUtil, times(1)).composeKey(anyString(), any(UUID.class));
            verify(mapper, times(1)).toRedisDto(any(CardInfo.class));
            verify(cardInfoCacheService, times(1)).updateCache(any(CacheType.class), anyString(),
                                                               any(CardCacheDto.class));
            verify(userCacheInvalidator, times(1)).invalidate(anyLong());
            verify(mapper, times(1)).toDto(any(CardInfo.class));
        }

        @Test
        void createShouldThrowExceptionUserNotFound() {

            long userId = 999L;
            CardInfoCreateDto createDto = new CardInfoCreateDto("88885555444433331111", "Test_2 With_Several_Cards",
                                                                LocalDate.of(2027, 11, 30));

            when(internalUserService.getUserById(userId)).thenThrow(UserNotFoundException.class);

            assertThrowsExactly(UserNotFoundException.class, () -> cardService.create(createDto, userId));
            verify(internalUserService, times(1)).getUserById(anyLong());
        }
    }

    @DisplayName("testing getById() method")
    @Nested
    class getById {

        @Test
        void getByIdPositiveReadFromDb() {

            long userId = testUser.getId();
            UUID cardId = testCard.getId();

            when(cardInfoCacheService.readFromCache(eq(CardCache.BY_ID), anyString())).thenReturn(Optional.empty());
            when(cardRepository.findByIdAndUserId(cardId, userId)).thenReturn(Optional.of(testCard));
            when(mapper.toRedisDto(testCard)).thenReturn(TestUtil.mapToCardRedisDto(testCard));
            doNothing().when(cardInfoCacheService).updateCache(eq(CardCache.BY_ID), anyString(),
                                                               any(CardCacheDto.class));
            CardInfoResponseDto expectedResult = TestUtil.mapToCardInfoResponseDto(testCard);
            when(mapper.toDto(testCard)).thenReturn(expectedResult);

            CardInfoResponseDto actualResult = cardService.getById(cardId, userId);

            assertEquals(expectedResult, actualResult);
            verify(cacheUtil, times(2)).composeKey(anyString(), any(UUID.class));
            verify(cardInfoCacheService, times(1)).readFromCache(any(CacheType.class), anyString());
            verify(cardRepository, times(1)).findByIdAndUserId(any(UUID.class), anyLong());
            verify(mapper, times(1)).toRedisDto(any(CardInfo.class));
            verify(cardInfoCacheService, times(1)).updateCache(any(CacheType.class), anyString(),
                                                               any(CardCacheDto.class));
            verify(mapper, times(1)).toDto(any(CardInfo.class));
        }

        @Test
        void getByIdPositiveReadFromCache() {

            long userId = testUser.getId();
            UUID cardId = testCard.getId();

            CardCacheDto cachedDto = TestUtil.mapToCardRedisDto(testCard);
            when(cardInfoCacheService.readFromCache(eq(CardCache.BY_ID), anyString())).thenReturn(
                    Optional.of(cachedDto));
            CardInfoResponseDto expectedResult = TestUtil.mapToCardInfoResponseDtoFromRedisDto(cachedDto);
            when(mapper.toDto(cachedDto)).thenReturn(expectedResult);

            CardInfoResponseDto actualResult = cardService.getById(cardId, userId);

            assertEquals(expectedResult, actualResult);
            verify(cacheUtil, times(1)).composeKey(anyString(), any(UUID.class));
            verify(cardInfoCacheService, times(1)).readFromCache(any(CacheType.class), anyString());
            verify(mapper, times(1)).toDto(any(CardCacheDto.class));
        }

        @Test
        void getByIdShouldThrowExceptionWhenNotFound() {

            long userId = testUser.getId();
            UUID cardId = UUID.randomUUID();

            when(cardInfoCacheService.readFromCache(eq(CardCache.BY_ID), anyString())).thenReturn(Optional.empty());
            when(cardRepository.findByIdAndUserId(cardId, userId)).thenReturn(Optional.empty());

            assertThrowsExactly(CardNotFoundException.class, () -> cardService.getById(cardId, userId));
            verify(cacheUtil, times(1)).composeKey(anyString(), any(UUID.class));
            verify(cardInfoCacheService, times(1)).readFromCache(any(CacheType.class), anyString());
            verify(cardRepository, times(1)).findByIdAndUserId(any(UUID.class), anyLong());
        }

    }

    @DisplayName("testing delete() method")
    @Nested
    class delete {

        @Test
        void deleteHappyPass() {

            long userId = testUser.getId();
            UUID cardId = testCard.getId();

            when(cardRepository.findByIdAndUserId(cardId, userId)).thenReturn(Optional.of(testCard));
            doNothing().when(cardRepository).delete(testCard);
            doNothing().when(cardInfoCacheService).removeFromCache(any(CacheType.class), anyString());
            doNothing().when(userCacheInvalidator).invalidate(anyLong());

            cardService.delete(cardId, userId);

            ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
            ArgumentCaptor<UUID> cardIdCaptor = ArgumentCaptor.forClass(UUID.class);

            verify(cardRepository, times(1))
                    .findByIdAndUserId(cardIdCaptor.capture(), userIdCaptor.capture());
            verify(cardRepository, times(1))
                    .delete(any(CardInfo.class));
            verify(cacheUtil, times(1))
                    .composeKey(anyString(), any(UUID.class));
            verify(cardInfoCacheService, times(1))
                    .removeFromCache(any(CacheType.class), anyString());
            verify(userCacheInvalidator, times(1))
                    .invalidate(userIdCaptor.capture());

            assertAll(
                    () -> assertEquals(userId, userIdCaptor.getAllValues().getFirst()),
                    () -> assertEquals(userId, userIdCaptor.getAllValues().get(1)),
                    () -> assertEquals(cardId, cardIdCaptor.getValue())
            );

        }

        @Test
        void deleteNotFoundCardByUserIdAndCardId() {

            long userId = testUser.getId();
            UUID cardId = testCard.getId();

            when(cardRepository.findByIdAndUserId(cardId, userId)).thenReturn(Optional.empty());

            assertThrowsExactly(CardNotFoundException.class, () -> cardService.delete(cardId, userId));

            ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
            ArgumentCaptor<UUID> cardIdCaptor = ArgumentCaptor.forClass(UUID.class);

            verify(cardRepository, times(1))
                    .findByIdAndUserId(cardIdCaptor.capture(), userIdCaptor.capture());

            assertAll(
                    () -> assertEquals(userId, userIdCaptor.getValue()),
                    () -> assertEquals(cardId, cardIdCaptor.getValue())
            );

        }
    }

    @DisplayName("testing getAll() method")
    @Nested
    class getAll {

        @Test
        void shouldReturnEmptyList() {

            long usedId = testUser.getId();
            testUser.getCards().clear();

            Pageable pageable = PageRequest.of(FIRST_PAGE, DEFAULT_PAGE_SIZE);
            doReturn(new PageImpl<>(Collections.emptyList(), pageable, testUser.getCards().size()))
                    .when(cardRepository).findAllByUserId(usedId, pageable);

            List<CardInfoResponseDto> actualResult = cardService.getAll(usedId, pageable);

            assertThat(actualResult).isEmpty();
            verify(cardRepository, times(1)).findAllByUserId(anyLong(), any(Pageable.class));
            verifyNoInteractions(mapper);
        }

        @Test
        void shouldReturnListOfCards() {

            long usedId = testUser.getId();

            Pageable pageable = PageRequest.of(FIRST_PAGE, DEFAULT_PAGE_SIZE);
            doReturn(new PageImpl<>(testUser.getCards(), pageable, testUser.getCards().size()))
                    .when(cardRepository).findAllByUserId(usedId, pageable);

            System.out.println("Test users cards: " + testUser.getCards());
            CardInfoResponseDto firstCardDto = TestUtil.mapToCardInfoResponseDto(testUser.getCards().getFirst());
            doReturn(firstCardDto).when(mapper).toDto(testUser.getCards().getFirst());
            CardInfoResponseDto secondCardDto = TestUtil.mapToCardInfoResponseDto(testUser.getCards().getLast());
            doReturn(secondCardDto).when(mapper).toDto(testUser.getCards().getLast());

            List<CardInfoResponseDto> expectedResult = List.of(firstCardDto, secondCardDto);

            List<CardInfoResponseDto> actualResult = cardService.getAll(usedId, pageable);

            assertAll(
                    () -> assertThat(actualResult).containsExactlyInAnyOrderElementsOf(expectedResult),
                    () -> assertThat(actualResult.size()).isEqualTo(expectedResult.size()),
                    () -> assertThat(actualResult).hasSizeLessThanOrEqualTo(DEFAULT_PAGE_SIZE),
                    () -> assertThat(actualResult).hasSizeLessThanOrEqualTo(MAX_PAGE_SIZE)
            );

            verify(cardRepository, times(1)).findAllByUserId(anyLong(), any(Pageable.class));
            verify(mapper, times(expectedResult.size())).toDto(any(CardInfo.class));
        }

    }

    @DisplayName("testing getAllByIds() method")
    @Nested
    class getAllByIds {

        @Test
        void getAllByIdsFoundAllOrPartially() {

            List<UUID> ids = generateMissingCardIds(3);
            List<UUID> existingIds = testUser.getCards().stream()
                                             .map(CardInfo::getId)
                                             .toList();
            ids.addAll(existingIds);

            doReturn(testUser.getCards()).when(cardRepository).findAllByIdIn(new HashSet<>(ids));

            CardInfoResponseDto firstCardDto = TestUtil.mapToCardInfoResponseDto(testUser.getCards().getFirst());
            doReturn(firstCardDto).when(mapper).toDto(testUser.getCards().getFirst());
            CardInfoResponseDto secondCardDto = TestUtil.mapToCardInfoResponseDto(testUser.getCards().getLast());
            doReturn(secondCardDto).when(mapper).toDto(testUser.getCards().getLast());
            List<CardInfoResponseDto> expectedResult = List.of(firstCardDto, secondCardDto);

            List<CardInfoResponseDto> actualResult = cardService.getAllByIds(ids);

            assertAll(
                    () -> assertThat(actualResult).containsExactlyInAnyOrderElementsOf(expectedResult),
                    () -> assertThat(actualResult.size()).isEqualTo(existingIds.size())
            );

            verify(cardRepository).findAllByIdIn(anyCollection());
            verify(mapper, times(expectedResult.size())).toDto(any(CardInfo.class));
        }

        @Test
        void getAllByIdsShouldReturnEmptyList() {

            List<UUID> ids = generateMissingCardIds(2);

            doReturn(Collections.emptyList()).when(cardRepository).findAllByIdIn(new HashSet<>(ids));

            List<CardInfoResponseDto> actualResult = cardService.getAllByIds(ids);
            assertThat(actualResult).isEmpty();
            verify(cardRepository).findAllByIdIn(anyCollection());
            verifyNoInteractions(mapper);
        }

    }

    @Nested
    class update {

        @Test
        void updateHappyPass() {

            long userId = testUser.getId();
            String updatedNumber = "000011122223333";
            CardInfoUpdateDto updateDto = new CardInfoUpdateDto(testCard.getId(),
                                                                updatedNumber,
                                                                testCard.getHolder(),
                                                                testCard.getExpirationDate(),
                                                                testCard.getVersion());

            doReturn(Optional.of(testCard))
                    .when(cardRepository).findByIdAndUserId(updateDto.id(), userId);
            doReturn(Optional.empty())
                    .when(cardRepository).findByNumber(updateDto.number());

            CardInfo updatedEntity = TestUtil.copyCard(testCard);
            TestUtil.updateCard(updateDto, updatedEntity);
            doReturn(updatedEntity)
                    .when(mapper).updateEntity(updateDto, testCard);

            long currentVersion = testCard.getVersion();
            LocalDateTime updatedDate = LocalDateTime.now();
            updatedEntity.setVersion(++currentVersion);
            updatedEntity.setUpdatedAt(updatedDate);
            doReturn(updatedEntity)
                    .when(cardRepository).saveAndFlush(any(CardInfo.class));

            doReturn(TestUtil.mapToCardRedisDto(updatedEntity))
                    .when(mapper).toRedisDto(updatedEntity);
            doNothing()
                    .when(cardInfoCacheService).updateCache(any(CacheType.class), anyString(), any(CardCacheDto.class));
            doNothing()
                    .when(userCacheInvalidator).invalidate(userId);
            CardInfoResponseDto expectedResult = TestUtil.mapToCardInfoResponseDto(updatedEntity);
            doReturn(expectedResult)
                    .when(mapper).toDto(updatedEntity);

            CardInfoResponseDto actualResult = cardService.update(updateDto, userId);

            assertThat(actualResult).isEqualTo(expectedResult);

            verify(cardRepository).findByIdAndUserId(any(UUID.class), anyLong());
            verify(validationUtil).checkIfDtoVersionIsOutdated(anyLong(), any(CardInfoUpdateDto.class));
            verify(cardRepository).findByNumber(anyString());
            verify(mapper).updateEntity(any(CardInfoUpdateDto.class), any(CardInfo.class));
            verify(cardRepository).saveAndFlush(any(CardInfo.class));
            verify(mapper).toRedisDto(any(CardInfo.class));
            verify(cacheUtil).composeKey(anyString(), any(UUID.class));
            verify(cardInfoCacheService).updateCache(any(CacheType.class), anyString(), any(CardCacheDto.class));
            verify(userCacheInvalidator).invalidate(anyLong());
            verify(mapper).toDto(any(CardInfo.class));
        }

        @Test
        void updatePositiveNoAnyChanges() {

            long userId = testUser.getId();
            CardInfoUpdateDto updateDto = new CardInfoUpdateDto(testCard.getId(),
                                                                testCard.getNumber(),
                                                                testCard.getHolder(),
                                                                testCard.getExpirationDate(),
                                                                testCard.getVersion());

            doReturn(Optional.of(testCard))
                    .when(cardRepository).findByIdAndUserId(updateDto.id(), userId);
            CardInfoResponseDto expectedResult = TestUtil.mapToCardInfoResponseDto(testCard);
            doReturn(expectedResult)
                    .when(mapper).toDto(testCard);

            CardInfoResponseDto actualResult = cardService.update(updateDto, userId);

            assertThat(actualResult).isEqualTo(expectedResult);

            verify(cardRepository).findByIdAndUserId(any(UUID.class), anyLong());
            verify(mapper).toDto(any(CardInfo.class));
            verifyNoInteractions(validationUtil, cacheUtil, cardInfoCacheService, userCacheInvalidator);
            verify(cardRepository, never()).findByNumber(anyString());
            verify(mapper, never()).updateEntity(any(CardInfoUpdateDto.class), any(CardInfo.class));
            verify(cardRepository, never()).saveAndFlush(any(CardInfo.class));
            verify(mapper, never()).toRedisDto(any(CardInfo.class));
        }

        @Test
        void updateShouldThrowExceptionWhenUpdatedCardNumberExistsAndAssignedToOtherCardId() {

            long userId = testUser.getId();
            CardInfo firstCard = testUser.getCards().getFirst();
            CardInfo secondCard = testUser.getCards().getLast();
            String updatedNumber = secondCard.getNumber();
            CardInfoUpdateDto updateDto = new CardInfoUpdateDto(firstCard.getId(),
                                                                updatedNumber,
                                                                firstCard.getHolder(),
                                                                firstCard.getExpirationDate(),
                                                                firstCard.getVersion());

            doReturn(Optional.of(firstCard))
                    .when(cardRepository).findByIdAndUserId(updateDto.id(), userId);
            doReturn(Optional.of(secondCard))
                    .when(cardRepository).findByNumber(updateDto.number());

            assertThrowsExactly(IllegalCardUpdateRequestException.class, () -> cardService.update(updateDto, userId));
            verify(cardRepository).findByIdAndUserId(any(UUID.class), anyLong());
            verify(validationUtil).checkIfDtoVersionIsOutdated(anyLong(), any(CardInfoUpdateDto.class));
            verify(cardRepository).findByNumber(anyString());
            verify(cardRepository, never()).saveAndFlush(any(CardInfo.class));
            verifyNoInteractions(mapper, cacheUtil, cardInfoCacheService, userCacheInvalidator);
        }

        @Test
        void updateShouldThrowExceptionWhenDbVersionHasChanged() {

            long userId = testUser.getId();
            String updatedNumber = "000011122223333";
            CardInfoUpdateDto updateDto = new CardInfoUpdateDto(testCard.getId(),
                                                                updatedNumber,
                                                                testCard.getHolder(),
                                                                testCard.getExpirationDate(),
                                                                testCard.getVersion());

            long currentVersion = testCard.getVersion();
            testCard.setVersion(++currentVersion);
            doReturn(Optional.of(testCard))
                    .when(cardRepository).findByIdAndUserId(updateDto.id(), userId);

            assertThrowsExactly(UpdateDtoVersionOutdatedException.class, () -> cardService.update(updateDto, userId));
            verify(cardRepository).findByIdAndUserId(any(UUID.class), anyLong());
            verify(validationUtil).checkIfDtoVersionIsOutdated(anyLong(), any(CardInfoUpdateDto.class));
            verifyNoInteractions(mapper, cacheUtil, cardInfoCacheService, userCacheInvalidator);
            verify(cardRepository, never()).findByNumber(anyString());
            verify(cardRepository, never()).saveAndFlush(any(CardInfo.class));
        }

        @Test
        void updateShouldThrowExceptionWhenCardNotFound() {

            long userId = testUser.getId();
            String updatedNumber = "000011122223333";
            CardInfoUpdateDto updateDto = new CardInfoUpdateDto(generateMissingCardIds(1).getFirst(),
                                                                updatedNumber,
                                                                testCard.getHolder(),
                                                                testCard.getExpirationDate(),
                                                                testCard.getVersion());

            doReturn(Optional.empty())
                    .when(cardRepository).findByIdAndUserId(updateDto.id(), userId);

            assertThrowsExactly(CardNotFoundException.class, () -> cardService.update(updateDto, userId));
            verify(cardRepository).findByIdAndUserId(any(UUID.class), anyLong());
            verifyNoInteractions(validationUtil, mapper, cacheUtil, cardInfoCacheService, userCacheInvalidator);
            verify(cardRepository, never()).findByNumber(anyString());
            verify(cardRepository, never()).saveAndFlush(any(CardInfo.class));
        }

    }

}