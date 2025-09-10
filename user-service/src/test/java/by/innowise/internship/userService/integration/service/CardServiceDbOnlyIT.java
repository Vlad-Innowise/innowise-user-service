package by.innowise.internship.userService.integration.service;

import by.innowise.common.library.exception.UpdateDtoVersionOutdatedException;
import by.innowise.common.library.exception.UserNotFoundException;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoCreateDto;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoResponseDto;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoUpdateDto;
import by.innowise.internship.userService.core.exception.CardNotFoundException;
import by.innowise.internship.userService.core.exception.IllegalCardUpdateRequestException;
import by.innowise.internship.userService.core.repository.CardInfoRepository;
import by.innowise.internship.userService.core.repository.entity.CardInfo;
import by.innowise.internship.userService.core.service.api.CardService;
import by.innowise.internship.userService.integration.core.IntegrationTestBaseDbOnly;
import by.innowise.internship.userService.integration.util.TestData;
import by.innowise.internship.userService.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class CardServiceDbOnlyIT extends IntegrationTestBaseDbOnly {

    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final int MAX_PAGE_SIZE = 50;
    private static final int FIRST_PAGE = 0;

    @Autowired
    private CardInfoRepository cardRepository;

    @Autowired
    private CardService cardService;

    static Stream<Arguments> checkIfCardNumberExists() {
        return Stream.of(
                Arguments.of(TestData.FRODO_BAGGINS.getCards().getFirst().getNumber(), true),
                Arguments.of("0000000000001111", false)
        );
    }

    @ParameterizedTest(name = "[{index}]: Check card number [{0}] awaiting result: {1}")
    @MethodSource("checkIfCardNumberExists")
    void cardNumberExists(String cardNumber, boolean expectedResult) {
        assertEquals(expectedResult, cardService.cardNumberExists(cardNumber));
    }

    @DisplayName("testing create() method")
    @Nested
    class create {

        @Test
        void createHappyPass() {

            long authId = TestData.ARYA_STARK.getAuthId();
            String newCardNumber = "8888555544443333";
            CardInfoCreateDto createDto = new CardInfoCreateDto(newCardNumber, "ARYA STARK",
                                                                LocalDate.of(2027, 11, 30));

            boolean isNewCardNumberExist = cardService.cardNumberExists(newCardNumber);
            Pageable pageable = PageRequest.of(FIRST_PAGE, DEFAULT_PAGE_SIZE);
            List<CardInfoResponseDto> initialCardList = cardService.getAll(TestData.ARYA_STARK.getId(), pageable);

            CardInfoResponseDto createdCard = cardService.create(createDto, authId);

            assertAll(
                    () -> assertThat(isNewCardNumberExist).isEqualTo(false),
                    () -> assertThat(initialCardList).isEmpty(),
                    () -> assertThat(createdCard.id()).isNotNull(),
                    () -> assertThat(createdCard.number()).isEqualTo(createDto.number()),
                    () -> assertThat(createdCard.holder()).isEqualTo(createDto.holder()),
                    () -> assertThat(createdCard.expirationDate()).isEqualTo(createDto.expirationDate())
            );
        }

        @Test
        void createShouldThrowExceptionUserNotFound() {

            long authId = 999L;
            String newCardNumber = "8888555544443333";
            CardInfoCreateDto createDto = new CardInfoCreateDto(newCardNumber, "ARYA STARK",
                                                                LocalDate.of(2027, 11, 30));

            boolean isNewCardNumberExist = cardService.cardNumberExists(newCardNumber);
            Pageable pageable = PageRequest.of(FIRST_PAGE, DEFAULT_PAGE_SIZE);
            List<CardInfoResponseDto> initialCardList = cardService.getAll(TestData.ARYA_STARK.getId(), pageable);

            assertAll(
                    () -> assertThat(isNewCardNumberExist).isEqualTo(false),
                    () -> assertThat(initialCardList).isEmpty(),
                    () -> assertThrowsExactly(UserNotFoundException.class, () -> cardService.create(createDto, authId))
            );
        }
    }


    @DisplayName("testing delete() method")
    @Nested
    class delete {

        @Test
        void deleteHappyPass() {

            long authId = TestData.SAMWISE_GAMPGIE.getAuthId();
            UUID cardId = TestData.SAMWISE_GAMPGIE.getCards().getFirst().getId();

            Pageable pageable = PageRequest.of(FIRST_PAGE, DEFAULT_PAGE_SIZE);
            List<CardInfoResponseDto> foundCards = cardService.getAll(authId, pageable);

            cardService.delete(cardId, authId);

            List<CardInfoResponseDto> foundCardsAfterDeletion = cardService.getAll(authId, pageable);

            assertAll(
                    () -> assertThat(foundCards.size()).isEqualTo(1),
                    () -> assertThat(foundCards.getFirst().id()).isEqualTo(cardId),
                    () -> assertThat(foundCardsAfterDeletion).isEmpty()
            );

        }

        @Test
        void deleteNotFoundCardByUserIdAndCardId() {

            long authId = TestData.SAMWISE_GAMPGIE.getAuthId();
            UUID cardId = UUID.randomUUID();

            Pageable pageable = PageRequest.of(FIRST_PAGE, DEFAULT_PAGE_SIZE);
            List<CardInfoResponseDto> foundCards = cardService.getAll(authId, pageable);

            assertAll(
                    () -> assertThat(foundCards.size()).isEqualTo(1),
                    () -> assertThat(foundCards.getFirst().id()).isNotEqualTo(cardId),
                    () -> assertThrowsExactly(CardNotFoundException.class, () -> cardService.delete(cardId, authId))
            );
        }
    }

    @DisplayName("testing getById() method")
    @Nested
    class getById {

        @Test
        void getByIdPositiveReadFromDb() {

            long authId = TestData.SAMWISE_GAMPGIE.getAuthId();
            UUID cardId = TestData.SAMWISE_GAMPGIE.getCards().getFirst().getId();

            CardInfoResponseDto expectedResult = TestUtil.mapToCardInfoResponseDto(
                    TestData.SAMWISE_GAMPGIE.getCards().getFirst());

            CardInfoResponseDto actualResult = cardService.getById(cardId, authId);

            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        void getByIdShouldThrowExceptionWhenNotFound() {

            long authId = TestData.SAMWISE_GAMPGIE.getAuthId();
            UUID cardId = UUID.randomUUID();

            assertThrowsExactly(CardNotFoundException.class, () -> cardService.getById(cardId, authId));
        }

    }

    @DisplayName("testing getAll() method")
    @Nested
    class getAll {

        @Test
        void shouldReturnEmptyList() {

            Pageable pageable = PageRequest.of(FIRST_PAGE, DEFAULT_PAGE_SIZE);

            List<CardInfoResponseDto> actualResult = cardService.getAll(TestData.ARYA_STARK.getAuthId(), pageable);

            assertThat(actualResult).isEmpty();
        }

        @Test
        void shouldReturnListOfCards() {

            Pageable pageable = PageRequest.of(FIRST_PAGE, DEFAULT_PAGE_SIZE);

            List<CardInfoResponseDto> actualResult = cardService.getAll(TestData.FRODO_BAGGINS.getAuthId(), pageable);

            List<CardInfoResponseDto> expectedResult =
                    TestData.FRODO_BAGGINS.getCards().stream()
                                          .map(TestUtil::mapToCardInfoResponseDto)
                                          .toList();

            assertAll(
                    () -> assertThat(actualResult).containsExactlyInAnyOrderElementsOf(expectedResult),
                    () -> assertThat(actualResult.size()).isEqualTo(expectedResult.size()),
                    () -> assertThat(actualResult).hasSizeLessThanOrEqualTo(DEFAULT_PAGE_SIZE),
                    () -> assertThat(actualResult).hasSizeLessThanOrEqualTo(MAX_PAGE_SIZE)
            );
        }

    }


    @DisplayName("testing getAllByIds() method")
    @Nested
    class getAllByIds {

        @Test
        void getAllByIdsFoundAllOrPartially() {

            Set<UUID> existingIds = Stream.of(TestData.FRODO_BAGGINS,
                                              TestData.SAMWISE_GAMPGIE)
                                          .flatMap(u -> u.getCards().stream())
                                          .map(CardInfo::getId)
                                          .collect(Collectors.toSet());

            List<UUID> missingIds = Stream.generate(UUID::randomUUID)
                                          .filter(uuid -> !existingIds.contains(uuid))
                                          .limit(3)
                                          .toList();

            List<UUID> idsToFind = new ArrayList<>(existingIds);
            idsToFind.addAll(missingIds);

            List<CardInfoResponseDto> expectedResult =
                    Stream.of(TestData.FRODO_BAGGINS,
                              TestData.SAMWISE_GAMPGIE)
                          .flatMap(u -> u.getCards().stream())
                          .map(TestUtil::mapToCardInfoResponseDto)
                          .toList();

            List<CardInfoResponseDto> actualResult = cardService.getAllByIds(idsToFind);

            assertAll(
                    () -> assertThat(actualResult).containsExactlyInAnyOrderElementsOf(expectedResult),
                    () -> assertThat(actualResult.size()).isEqualTo(expectedResult.size())
            );

        }

        @Test
        void getAllByIdsShouldReturnEmptyList() {

            Set<UUID> existingIds = Stream.of(TestData.FRODO_BAGGINS,
                                              TestData.SAMWISE_GAMPGIE)
                                          .flatMap(u -> u.getCards().stream())
                                          .map(CardInfo::getId)
                                          .collect(Collectors.toSet());

            List<UUID> missingIds = Stream.generate(UUID::randomUUID)
                                          .filter(uuid -> !existingIds.contains(uuid))
                                          .limit(3)
                                          .toList();

            List<CardInfoResponseDto> actualResult = cardService.getAllByIds(missingIds);

            assertThat(actualResult).isEmpty();
        }
    }

    @DisplayName("testing update() method")
    @Nested
    class update {

        @Test
        void updateHappyPass() {

            long authId = TestData.SAMWISE_GAMPGIE.getAuthId();
            CardInfo initialCard = TestUtil.copyCard(TestData.SAMWISE_GAMPGIE.getCards().getFirst());

            String updatedNumber = "000011122223333";
            CardInfoUpdateDto updateDto = new CardInfoUpdateDto(initialCard.getId(),
                                                                updatedNumber,
                                                                initialCard.getHolder(),
                                                                initialCard.getExpirationDate(),
                                                                initialCard.getVersion());

            CardInfoResponseDto actualResult = cardService.update(updateDto, authId);

            assertAll(
                    () -> assertThat(actualResult.number()).isEqualTo(updatedNumber),
                    () -> assertThat(actualResult.holder()).isEqualTo(initialCard.getHolder()),
                    () -> assertThat(actualResult.expirationDate()).isEqualTo(initialCard.getExpirationDate()),
                    () -> assertThat(actualResult.getVersion()).isEqualTo(initialCard.getVersion() + 1)
            );
        }

        @Test
        void updatePositiveNoAnyChanges() {

            long authId = TestData.SAMWISE_GAMPGIE.getAuthId();
            CardInfo initialCard = TestUtil.copyCard(TestData.SAMWISE_GAMPGIE.getCards().getFirst());

            CardInfoUpdateDto updateDto = new CardInfoUpdateDto(initialCard.getId(),
                                                                initialCard.getNumber(),
                                                                initialCard.getHolder(),
                                                                initialCard.getExpirationDate(),
                                                                initialCard.getVersion());

            CardInfoResponseDto expectedResult = TestUtil.mapToCardInfoResponseDto(initialCard);

            CardInfoResponseDto actualResult = cardService.update(updateDto, authId);

            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        void updateShouldThrowExceptionWhenUpdatedCardNumberExistsAndAssignedToOtherCardId() {

            long authId = TestData.FRODO_BAGGINS.getAuthId();
            CardInfo firstCard = TestUtil.copyCard(TestData.FRODO_BAGGINS.getCards().getFirst());
            CardInfo secondCard = TestUtil.copyCard(TestData.SAMWISE_GAMPGIE.getCards().getFirst());

            String updatedNumber = secondCard.getNumber();
            CardInfoUpdateDto updateDto = new CardInfoUpdateDto(firstCard.getId(),
                                                                updatedNumber,
                                                                firstCard.getHolder(),
                                                                firstCard.getExpirationDate(),
                                                                firstCard.getVersion());

            assertThrowsExactly(IllegalCardUpdateRequestException.class, () -> cardService.update(updateDto, authId));
        }

        @Test
        void updateShouldThrowExceptionWhenDbVersionHasChanged() {

            long authId = TestData.SAMWISE_GAMPGIE.getAuthId();
            CardInfo initialCard = TestUtil.copyCard(TestData.SAMWISE_GAMPGIE.getCards().getFirst());

            String updatedNumber = "000011122223333";
            CardInfoUpdateDto updateDto = new CardInfoUpdateDto(initialCard.getId(),
                                                                updatedNumber,
                                                                initialCard.getHolder(),
                                                                initialCard.getExpirationDate(),
                                                                initialCard.getVersion());

            String anotherUpdatedNumber = "9999999999999999";
            cardRepository.findById(initialCard.getId())
                          .ifPresent(c -> {
                                         c.setNumber(anotherUpdatedNumber);
                                         cardRepository.saveAndFlush(c);
                                     }
                          );

            assertThrowsExactly(UpdateDtoVersionOutdatedException.class, () -> cardService.update(updateDto, authId));
        }

        @Test
        void updateShouldThrowExceptionWhenCardNotFound() {

            long authId = TestData.SAMWISE_GAMPGIE.getAuthId();
            CardInfo initialCard = TestUtil.copyCard(TestData.SAMWISE_GAMPGIE.getCards().getFirst());
            String updatedNumber = "000011122223333";
            CardInfoUpdateDto updateDto = new CardInfoUpdateDto(UUID.randomUUID(),
                                                                updatedNumber,
                                                                initialCard.getHolder(),
                                                                initialCard.getExpirationDate(),
                                                                initialCard.getVersion());

            assertThrowsExactly(CardNotFoundException.class, () -> cardService.update(updateDto, authId));
        }

    }
}
