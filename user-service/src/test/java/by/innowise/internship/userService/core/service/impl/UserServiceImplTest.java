package by.innowise.internship.userService.core.service.impl;

import by.innowise.internship.userService.TestUtil;
import by.innowise.internship.userService.api.dto.user.UserCreateDto;
import by.innowise.internship.userService.api.dto.user.UserResponseDto;
import by.innowise.internship.userService.api.dto.user.UserUpdateDto;
import by.innowise.internship.userService.core.cache.CacheUtil;
import by.innowise.internship.userService.core.cache.UserCacheService;
import by.innowise.internship.userService.core.cache.dto.UserCacheDto;
import by.innowise.internship.userService.core.cache.supportedCaches.CacheType;
import by.innowise.internship.userService.core.cache.supportedCaches.UserCache;
import by.innowise.internship.userService.core.exception.UniqueConstraintViolationException;
import by.innowise.internship.userService.core.exception.UpdateDtoVersionOutdatedException;
import by.innowise.internship.userService.core.exception.UserNotFoundException;
import by.innowise.internship.userService.core.mapper.UserMapper;
import by.innowise.internship.userService.core.repository.UserRepository;
import by.innowise.internship.userService.core.repository.entity.CardInfo;
import by.innowise.internship.userService.core.repository.entity.User;
import by.innowise.internship.userService.core.util.validation.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper mapper;

    @Spy
    private ValidationUtil validationUtil;

    @Mock
    private UserCacheService userCacheService;

    @Spy
    private CacheUtil cacheUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private User userWithSeveralCards;

    private User userWithNoCards;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void init() {
        this.userWithNoCards = TestUtil.getUser(1L, "Test", "No_Cards", LocalDate.of(1980, 1, 10),
                                                "no_cards@email.com");
        List<CardInfo> cards = List.of(
                TestUtil.getCard("4444555566667777", "Test_2 With_Several_Cards", LocalDate.of(2030, 10, 31)),
                TestUtil.getCard("6666777788889999", "Test_2 With_Several_Cards", LocalDate.of(2029, 6, 30))
        );
        this.userWithSeveralCards = TestUtil.getUser(2L, "Test_2", "With_Several_Cards", LocalDate.of(1990, 12, 13),
                                                     "several_cards@email.com"
        );
        cards.forEach(c -> {
            c.setUser(userWithSeveralCards);
            userWithSeveralCards.addCard(c);
        });

    }

    @Test
    void createHappyPass() {

        UserCreateDto userCreateDto = TestUtil.getUserCreateDtoFromUser(userWithNoCards);

        User toSave = TestUtil.copyUser(userWithNoCards);
        toSave.setId(null);
        toSave.setCreatedAt(null);
        toSave.setUpdatedAt(null);

        when(mapper.toEntity(userCreateDto)).thenReturn(toSave);
        when(userRepository.saveAndFlush(toSave)).thenReturn(userWithNoCards);
        when(mapper.toRedisDto(userWithNoCards)).thenReturn(TestUtil.mapToUserRedisDto(userWithNoCards));
        doNothing().when(userCacheService).updateCache(any(CacheType.class), any(String.class),
                                                       any(UserCacheDto.class));

        UserResponseDto expectedResult = TestUtil.getUserResponseDto(userWithNoCards);
        when(mapper.toDto(userWithNoCards)).thenReturn(expectedResult);

        UserResponseDto actualResult = userService.create(userCreateDto);

        verify(mapper, times(1)).toEntity(any(UserCreateDto.class));
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
        verify(mapper, times(1)).toRedisDto(any(User.class));
        verify(cacheUtil, times(1)).composeKey(any(String.class), anyLong());
        verify(userCacheService, times(1)).updateCache(any(CacheType.class), any(String.class),
                                                       any(UserCacheDto.class));
        verify(mapper, times(1)).toDto(any(User.class));

        assertEquals(expectedResult, actualResult);
        assertTrue(actualResult.cards().isEmpty());
    }

    @Test
    void isEmailExistsShouldReturnTrue() {

        String givenEmail = "no_cards@email.com";

        when(userRepository.findByEmail(eq(givenEmail))).thenReturn(Optional.of(userWithNoCards));

        assertTrue(userService.isEmailExists(givenEmail));
        verify(userRepository, times(1)).findByEmail(givenEmail);
    }

    @Test
    void isEmailExistsShouldReturnFalse() {

        String givenEmail = "missing@email.com";

        when(userRepository.findByEmail(eq(givenEmail))).thenReturn(Optional.empty());

        assertFalse(userService.isEmailExists(givenEmail));
        verify(userRepository, times(1)).findByEmail(givenEmail);
    }


    @Test
    void getUserByIdPositive() {

        long id = 2L;

        when(userRepository.findById(eq(id))).thenReturn(Optional.of(userWithSeveralCards));

        User actualResult = userService.getUserById(id);
        assertEquals(userWithSeveralCards, actualResult);
        assertEquals(2, actualResult.getCards().size());
        verify(userRepository, times(1)).findById(id);
    }


    @Test
    void getUserByIdShouldThrowExceptionWhenNotFound() {

        long id = 999L;

        when(userRepository.findById(eq(id))).thenReturn(Optional.empty());

        assertThrowsExactly(UserNotFoundException.class, () -> userService.getUserById(id));
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void getByIdPositiveReadFromDb() {

        long userId = 2L;

        when(userCacheService.readFromCache(eq(UserCache.BY_ID), any(String.class))).thenReturn(Optional.empty());
        when(userRepository.findByIdWithAllCards(userId)).thenReturn(Optional.of(userWithSeveralCards));
        UserCacheDto cacheDto = TestUtil.mapToUserRedisDto(userWithSeveralCards);
        when(mapper.toRedisDto(userWithSeveralCards)).thenReturn(cacheDto);
        doNothing().when(userCacheService).updateCache(eq(UserCache.BY_ID), any(String.class), eq(cacheDto));
        UserResponseDto expectedResult = TestUtil.getUserResponseDto(userWithSeveralCards);
        when(mapper.toDto(userWithSeveralCards)).thenReturn(expectedResult);

        UserResponseDto actualResult = userService.getById(userId);

        assertEquals(expectedResult, actualResult);
        assertEquals(expectedResult.cards().size(), actualResult.cards().size());
        verify(cacheUtil, times(2)).composeKey(any(String.class), anyLong());
        verify(userCacheService, times(1)).readFromCache(any(CacheType.class), any(String.class));
        verify(userRepository, times(1)).findByIdWithAllCards(anyLong());
        verify(mapper, times(1)).toRedisDto(any(User.class));
        verify(userCacheService, times(1)).updateCache(any(CacheType.class), any(String.class),
                                                       any(UserCacheDto.class));
        verify(mapper, times(1)).toDto(any(User.class));
    }

    @Test
    void getByIdPositiveReadFromCache() {

        long userId = 2L;

        UserCacheDto userCachedDto = TestUtil.mapToUserRedisDto(userWithSeveralCards);
        when(userCacheService.readFromCache(eq(UserCache.BY_ID), any(String.class))).thenReturn(
                Optional.of(userCachedDto));
        UserResponseDto expectedResult = TestUtil.getUserResponseDtoFromRedisDto(userCachedDto);
        when(mapper.toDto(eq(userCachedDto))).thenReturn(expectedResult);

        UserResponseDto actualResult = userService.getById(userId);

        assertEquals(expectedResult, actualResult);
        assertEquals(expectedResult.cards().size(), actualResult.cards().size());
        verify(cacheUtil, times(1)).composeKey(any(String.class), anyLong());
        verify(userCacheService, times(1)).readFromCache(any(CacheType.class), any(String.class));
        verify(mapper, times(1)).toDto(any(UserCacheDto.class));
    }

    @Test
    void getByIdShouldThrowExceptionWhenNotFound() {

        long userId = 2L;

        when(userCacheService.readFromCache(eq(UserCache.BY_ID), any(String.class))).thenReturn(Optional.empty());
        when(userRepository.findByIdWithAllCards(userId)).thenReturn(Optional.empty());

        assertThrowsExactly(UserNotFoundException.class, () -> userService.getById(userId));
    }

    @Test
    void deletePositive() {

        long userId = 2L;

        when(userRepository.findByIdWithAllCards(userId)).thenReturn(Optional.of(userWithSeveralCards));
        doNothing().when(userRepository).delete(userWithSeveralCards);
        doNothing().when(userCacheService).removeFromCache(eq(UserCache.BY_ID), any(String.class));

        userService.delete(userId);
        verify(userRepository, times(1)).findByIdWithAllCards(anyLong());
        verify(userRepository, times(1)).delete(any(User.class));
        verify(cacheUtil, times(1)).composeKey(any(String.class), anyLong());
        verify(userCacheService).removeFromCache(any(CacheType.class), any(String.class));
    }

    @Test
    void deleteShouldThrowExceptionWhenNotFound() {

        long userId = 2L;

        when(userRepository.findByIdWithAllCards(userId)).thenReturn(Optional.empty());

        assertThrowsExactly(UserNotFoundException.class, () -> userService.delete(userId));
    }

    @Test
    void getAllByIdsPositiveFoundAllOrPartially() {

        List<Long> ids = List.of(1L, 2L, 999L);

        List<User> foundUsers = List.of(userWithNoCards, userWithSeveralCards);
        when(userRepository.findByIdIn(new HashSet<>(ids))).thenReturn(foundUsers);
        UserResponseDto userWithNoCardsResponse = TestUtil.getUserResponseDto(userWithNoCards);
        when(mapper.toDto(userWithNoCards)).thenReturn(userWithNoCardsResponse);
        UserResponseDto userWithSeveralCardsResponse = TestUtil.getUserResponseDto(userWithSeveralCards);
        when(mapper.toDto(userWithSeveralCards)).thenReturn(userWithSeveralCardsResponse);
        List<UserResponseDto> expectedResult = List.of(userWithNoCardsResponse, userWithSeveralCardsResponse);

        List<UserResponseDto> actualResult = userService.getAllByIds(ids);

        assertEquals(expectedResult.size(), actualResult.size());
        assertThat(actualResult).hasSameElementsAs(expectedResult);
        verify(userRepository, times(1)).findByIdIn(anySet());
        verify(mapper, times(expectedResult.size())).toDto(any(User.class));
    }

    @Test
    void getAllByIdsEmptyListWhenNotFoundAny() {

        List<Long> ids = List.of(998L, 999L);

        when(userRepository.findByIdIn(new HashSet<>(ids))).thenReturn(Collections.emptyList());

        List<UserResponseDto> actualResult = userService.getAllByIds(ids);
        assertTrue(actualResult.isEmpty());
        verify(mapper, times(0)).toDto(any(User.class));
    }

    @Test
    void updateHappyPass() {

        long userId = 2L;
        User initUser = userWithSeveralCards;
        UserUpdateDto updateDto = new UserUpdateDto("UPD_Test_2", initUser.getSurname(), initUser.getBirthDate(),
                                                    "upd_several_cards@email.com", initUser.getVersion());

        when(userRepository.findByIdWithAllCards(userId)).thenReturn(Optional.of(initUser));
        when(userRepository.findByEmail(updateDto.email())).thenReturn(Optional.empty());

        User updated = TestUtil.copyUser(initUser);
        TestUtil.updateUser(updateDto, updated);
        when(mapper.updateEntity(updateDto, initUser)).thenReturn(updated);

        long currentVersion = initUser.getVersion();
        LocalDateTime updatedDate = LocalDateTime.now();
        updated.setVersion(++currentVersion);
        updated.setUpdatedAt(updatedDate);
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(updated);

        when(mapper.toRedisDto(updated)).thenReturn(TestUtil.mapToUserRedisDto(updated));
        doNothing().when(userCacheService).updateCache(any(CacheType.class), any(String.class),
                                                       any(UserCacheDto.class));

        UserResponseDto expectedResult = TestUtil.getUserResponseDto(updated);
        when(mapper.toDto(updated)).thenReturn(expectedResult);

        UserResponseDto actualResult = userService.update(updateDto, userId);

        assertEquals(expectedResult, actualResult);
        verify(userRepository, times(1)).findByIdWithAllCards(anyLong());
        verify(validationUtil, times(1)).checkIfDtoVersionIsOutdated(anyLong(), any(UserUpdateDto.class));
        verify(userRepository, times(1)).findByEmail(any(String.class));
        verify(mapper, times(1)).updateEntity(any(UserUpdateDto.class), any(User.class));
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
        verify(mapper, times(1)).toRedisDto(any(User.class));
        verify(cacheUtil, times(1)).composeKey(any(String.class), anyLong());
        verify(userCacheService, times(1)).updateCache(any(CacheType.class), any(String.class),
                                                       any(UserCacheDto.class));
        verify(mapper, times(1)).toDto(userCaptor.capture());

        User returnedFromDb = userCaptor.getValue();
        assertEquals(currentVersion, returnedFromDb.getVersion());
        assertEquals(updatedDate, returnedFromDb.getUpdatedAt());
    }

    @Test
    void updatePositiveNoAnyChanges() {

        long userId = 2L;
        User initUser = userWithSeveralCards;
        UserUpdateDto updateDto = new UserUpdateDto(initUser.getName(), initUser.getSurname(), initUser.getBirthDate(),
                                                    initUser.getEmail(), initUser.getVersion());

        when(userRepository.findByIdWithAllCards(userId)).thenReturn(Optional.of(initUser));
        UserResponseDto expectedResult = TestUtil.getUserResponseDto(initUser);
        when(mapper.toDto(initUser)).thenReturn(expectedResult);

        UserResponseDto actualResult = userService.update(updateDto, userId);

        assertEquals(expectedResult, actualResult);
        verify(validationUtil, times(0)).checkIfDtoVersionIsOutdated(anyLong(), any(UserUpdateDto.class));
        verify(mapper, times(1)).toDto(userCaptor.capture());

        User returnedFromDb = userCaptor.getValue();
        assertEquals(initUser.getVersion(), actualResult.getVersion());
        assertEquals(initUser.getUpdatedAt(), returnedFromDb.getUpdatedAt());
    }

    @Test
    void updateShouldThrowExceptionWhenUpdatedEmailAlreadyExists() {

        long userId = 2L;
        User initUser = userWithSeveralCards;
        UserUpdateDto updateDto = new UserUpdateDto(initUser.getName(), initUser.getSurname(), initUser.getBirthDate(),
                                                    "no_cards@email.com", initUser.getVersion());

        when(userRepository.findByIdWithAllCards(userId)).thenReturn(Optional.of(initUser));
        when(userRepository.findByEmail(updateDto.email())).thenReturn(Optional.of(userWithNoCards));

        assertThrowsExactly(UniqueConstraintViolationException.class, () -> userService.update(updateDto, userId));
        verify(userRepository, times(1)).findByIdWithAllCards(anyLong());
        verify(validationUtil, times(1)).checkIfDtoVersionIsOutdated(anyLong(), any(UserUpdateDto.class));
        verify(userRepository, times(1)).findByEmail(any(String.class));
    }

    @Test
    void updateShouldThrowExceptionWhenDbVersionHasChanged() {

        long userId = 2L;
        User initUser = userWithSeveralCards;
        UserUpdateDto updateDto = new UserUpdateDto("UPD_Test_2", initUser.getSurname(), initUser.getBirthDate(),
                                                    "upd_several_cards@email.com", initUser.getVersion());

        long currentVersion = initUser.getVersion();
        initUser.setVersion(++currentVersion);
        when(userRepository.findByIdWithAllCards(userId)).thenReturn(Optional.of(initUser));

        assertThrowsExactly(UpdateDtoVersionOutdatedException.class, () -> userService.update(updateDto, userId));
        verify(userRepository, times(1)).findByIdWithAllCards(anyLong());
        verify(validationUtil, times(1)).checkIfDtoVersionIsOutdated(anyLong(), any(UserUpdateDto.class));
    }

    @Test
    void updateShouldThrowExceptionWhenUserNotFound() {

        long userId = 999L;
        User initUser = userWithSeveralCards;
        UserUpdateDto updateDto = new UserUpdateDto("UPD_Test_2", initUser.getSurname(), initUser.getBirthDate(),
                                                    "upd_several_cards@email.com", initUser.getVersion());

        when(userRepository.findByIdWithAllCards(userId)).thenReturn(Optional.empty());

        assertThrowsExactly(UserNotFoundException.class, () -> userService.update(updateDto, userId));
        verify(userRepository, times(1)).findByIdWithAllCards(anyLong());
    }
}