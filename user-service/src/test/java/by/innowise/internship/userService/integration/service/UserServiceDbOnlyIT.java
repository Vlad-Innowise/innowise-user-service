package by.innowise.internship.userService.integration.service;

import by.innowise.common.library.exception.UniqueConstraintViolationException;
import by.innowise.common.library.exception.UpdateDtoVersionOutdatedException;
import by.innowise.common.library.exception.UserNotFoundException;
import by.innowise.internship.userService.api.dto.user.UserCreateDto;
import by.innowise.internship.userService.api.dto.user.UserResponseDto;
import by.innowise.internship.userService.api.dto.user.UserUpdateDto;
import by.innowise.internship.userService.core.repository.UserRepository;
import by.innowise.internship.userService.core.repository.entity.User;
import by.innowise.internship.userService.core.service.api.UserService;
import by.innowise.internship.userService.integration.core.IntegrationTestBaseDbOnly;
import by.innowise.internship.userService.integration.util.TestData;
import by.innowise.internship.userService.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class UserServiceDbOnlyIT extends IntegrationTestBaseDbOnly {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void init() {
        Set<User> initialTestUsers = Set.of(TestData.FRODO_BAGGINS, TestData.SAMWISE_GAMPGIE, TestData.ARYA_STARK);
        validateTestData(initialTestUsers);

    }

    private void validateTestData(Set<User> initialTestUsers) {
        Optional<User> chechFrodo = userRepository.findByIdWithAllCards(TestData.FRODO_BAGGINS.getId());
        Optional<User> chechSam = userRepository.findByIdWithAllCards(TestData.SAMWISE_GAMPGIE.getId());
        Optional<User> chechArya = userRepository.findByIdWithAllCards(TestData.ARYA_STARK.getId());

        long validUsersCount = Stream.of(chechFrodo, chechSam, chechArya)
                                     .filter(Optional::isPresent)
                                     .map(Optional::get)
                                     .filter(initialTestUsers::contains)
                                     .count();

        if (initialTestUsers.size() != validUsersCount) {
            throw new IllegalArgumentException("Invalid initial data" + initialTestUsers);
        }
    }


    @Test
    void createHappyPass() {

        UserCreateDto userCreateDto = new UserCreateDto("Taiwen", "Lanister", LocalDate.of(1970, 10, 18),
                                                        "taiwen_lanister@email.com");

        UserResponseDto actualResult = userService.create(userCreateDto);

        assertAll(
                () -> assertThat(actualResult.id()).isNotNull(),
                () -> assertThat(actualResult.name()).isEqualTo(userCreateDto.name()),
                () -> assertThat(actualResult.surname()).isEqualTo(userCreateDto.surname()),
                () -> assertThat(actualResult.email()).isEqualTo(userCreateDto.email()),
                () -> assertThat(actualResult.getVersion()).isNotNull(),
                () -> assertThat(actualResult.cards()).isEmpty()
        );
    }

    @Test
    void ifEmailExistsShouldReturnTrue() {

        String givenEmail = TestData.SAMWISE_GAMPGIE.getEmail();

        assertThat(userService.isEmailExists(givenEmail)).isEqualTo(true);
    }

    @Test
    void ifEmailNotExistsShouldReturnFalse() {

        Set<String> existingEmails = Stream.of(TestData.FRODO_BAGGINS,
                                               TestData.SAMWISE_GAMPGIE,
                                               TestData.ARYA_STARK)
                                           .map(User::getEmail)
                                           .collect(Collectors.toSet());

        String givenEmail = "dummy_email@email.com";

        assertAll(
                () -> assertThat(existingEmails.contains(givenEmail)).isEqualTo(false),
                () -> assertThat(userService.isEmailExists(givenEmail)).isEqualTo(false)
        );

    }

    @Test
    void getByIdPositiveReadFromDb() {

        long userId = TestData.FRODO_BAGGINS.getId();

        UserResponseDto expectedResult = TestUtil.mapToUserResponseDto(TestData.FRODO_BAGGINS);

        UserResponseDto actualResult = userService.getById(userId);

        assertAll(
                () -> assertEquals(expectedResult, actualResult),
                () -> assertEquals(expectedResult.cards().size(), actualResult.cards().size())
        );

    }

    @Test
    void getByIdShouldThrowExceptionWhenNotFound() {

        long userId = 999L;

        assertThrowsExactly(UserNotFoundException.class, () -> userService.getById(userId));
    }


    @Test
    void deletePositive() {

        long userId = TestData.FRODO_BAGGINS.getId();

        Optional<User> optionalUserBeforeDeletion = userRepository.findById(userId);
        userService.delete(userId);
        Optional<User> optionalUserAfterDeletion = userRepository.findById(userId);

        assertAll(
                () -> assertThat(optionalUserBeforeDeletion.isPresent()).isEqualTo(true),
                () -> assertThat(optionalUserAfterDeletion.isPresent()).isEqualTo(false)
        );
    }

    @Test
    void deleteShouldThrowExceptionWhenNotFound() {

        long userId = 999L;
        Optional<User> optionalUserBeforeDeletion = userRepository.findById(userId);

        assertAll(
                () -> assertThat(optionalUserBeforeDeletion.isPresent()).isEqualTo(false),
                () -> assertThrowsExactly(UserNotFoundException.class, () -> userService.delete(userId))
        );
    }


    @Test
    void getAllByIdsPositiveFoundAllOrPartially() {


        List<Long> existingIds = Stream.of(TestData.FRODO_BAGGINS,
                                           TestData.SAMWISE_GAMPGIE,
                                           TestData.ARYA_STARK)
                                       .map(User::getId)
                                       .toList();

        List<Long> missingIds = List.of(997L, 998L, 999L);

        List<Long> idsToFind = Stream.concat(existingIds.stream(),
                                             missingIds.stream())
                                     .toList();

        List<UserResponseDto> expectedResult = Stream.of(TestData.FRODO_BAGGINS,
                                                         TestData.SAMWISE_GAMPGIE,
                                                         TestData.ARYA_STARK)
                                                     .map(TestUtil::mapToUserResponseDto)
                                                     .toList();

        List<UserResponseDto> actualResult = userService.getAllByIds(idsToFind);

        assertAll(
                () -> assertThat(actualResult).containsExactlyInAnyOrderElementsOf(expectedResult),
                () -> assertThat(actualResult.size()).isEqualTo(existingIds.size())
        );
    }

    @Test
    void getAllByIdsEmptyListWhenNotFoundAny() {

        List<Long> missingIds = List.of(997L, 998L, 999L);

        List<UserResponseDto> actualResult = userService.getAllByIds(missingIds);

        assertThat(actualResult).isEmpty();
    }

    @Test
    void updateHappyPass() {

        long userId = TestData.FRODO_BAGGINS.getId();
        User initialUser = TestUtil.deepCopyUser(TestData.FRODO_BAGGINS);

        UserUpdateDto updateDto = new UserUpdateDto("Updated Frodo",
                                                    initialUser.getSurname(),
                                                    initialUser.getBirthDate(),
                                                    "upd_frodo_baggins@email.com",
                                                    initialUser.getVersion());

        UserResponseDto actualResult = userService.update(updateDto, userId);

        assertAll(
                () -> assertThat(actualResult.name()).isEqualTo(updateDto.name()),
                () -> assertThat(actualResult.surname()).isEqualTo(updateDto.surname()),
                () -> assertThat(actualResult.email()).isEqualTo(updateDto.email()),
                () -> assertThat(actualResult.birthDate()).isEqualTo(updateDto.birthDate()),
                () -> assertThat(actualResult.getVersion()).isEqualTo(updateDto.getVersion() + 1)
        );
    }

    @Test
    void updatePositiveNoAnyChanges() {

        long userId = TestData.FRODO_BAGGINS.getId();
        User initialUser = TestUtil.deepCopyUser(TestData.FRODO_BAGGINS);

        UserUpdateDto updateDto = new UserUpdateDto(initialUser.getName(),
                                                    initialUser.getSurname(),
                                                    initialUser.getBirthDate(),
                                                    initialUser.getEmail(),
                                                    initialUser.getVersion());

        UserResponseDto expectedResult = TestUtil.mapToUserResponseDto(initialUser);

        UserResponseDto actualResult = userService.update(updateDto, userId);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void updateShouldThrowExceptionWhenUpdatedEmailAlreadyExists() {

        long userId = TestData.FRODO_BAGGINS.getId();
        User initialUser = TestUtil.deepCopyUser(TestData.FRODO_BAGGINS);

        UserUpdateDto updateDto = new UserUpdateDto("Updated Frodo",
                                                    initialUser.getSurname(),
                                                    initialUser.getBirthDate(),
                                                    TestData.ARYA_STARK.getEmail(),
                                                    initialUser.getVersion());

        Optional<User> foundByEmail = userRepository.findByEmail(updateDto.email());

        assertAll(
                () -> assertThat(foundByEmail.isPresent()).isEqualTo(true),
                () -> assertThat(foundByEmail.orElseThrow()).isNotEqualTo(initialUser),
                () -> assertThrowsExactly(UniqueConstraintViolationException.class,
                                          () -> userService.update(updateDto, userId))
        );

    }

    @Test
    void updateShouldThrowExceptionWhenDbVersionHasChanged() {

        long userId = TestData.FRODO_BAGGINS.getId();
        User initialUser = TestUtil.deepCopyUser(TestData.FRODO_BAGGINS);

        UserUpdateDto updateDto = new UserUpdateDto("Updated Frodo",
                                                    initialUser.getSurname(),
                                                    initialUser.getBirthDate(),
                                                    "upd_frodo_baggins@email.com",
                                                    initialUser.getVersion());

        String anotherUpdatedSurname = "upd_Baggins";
        userRepository.findByIdWithAllCards(initialUser.getId())
                      .ifPresent(u -> {
                                     u.setSurname(anotherUpdatedSurname);
                                     userRepository.saveAndFlush(u);
                                 }
                      );

        assertThrowsExactly(UpdateDtoVersionOutdatedException.class, () -> userService.update(updateDto, userId));
    }

    @Test
    void updateShouldThrowExceptionWhenUserNotFound() {

        long userId = 999L;
        User initialUser = TestUtil.deepCopyUser(TestData.FRODO_BAGGINS);
        UserUpdateDto updateDto = new UserUpdateDto("Updated Frodo",
                                                    initialUser.getSurname(),
                                                    initialUser.getBirthDate(),
                                                    "upd_frodo_baggins@email.com",
                                                    initialUser.getVersion());

        Optional<User> optionalUser = userRepository.findById(userId);

        assertAll(
                () -> assertThat(optionalUser.isPresent()).isEqualTo(false),
                () -> assertThrowsExactly(UserNotFoundException.class, () -> userService.update(updateDto, userId))
        );

    }
}
