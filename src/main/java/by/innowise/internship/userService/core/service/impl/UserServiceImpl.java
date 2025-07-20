package by.innowise.internship.userService.core.service.impl;

import by.innowise.internship.userService.api.dto.user.UserCreateDto;
import by.innowise.internship.userService.api.dto.user.UserResponseDto;
import by.innowise.internship.userService.api.dto.user.UserUpdateDto;
import by.innowise.internship.userService.core.exception.UniqueConstraintViolationException;
import by.innowise.internship.userService.core.exception.UserNotFoundException;
import by.innowise.internship.userService.core.mapper.UserMapper;
import by.innowise.internship.userService.core.repository.UserRepository;
import by.innowise.internship.userService.core.repository.entity.User;
import by.innowise.internship.userService.core.service.api.InternalUserService;
import by.innowise.internship.userService.core.service.api.UserService;
import by.innowise.internship.userService.core.util.validation.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, InternalUserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final ValidationUtil validationUtil;

    @Transactional
    @Override
    public UserResponseDto create(UserCreateDto dto) {
        User toSave = mapper.toEntity(dto);
        log.info("Invoking user repository to save user: {}", toSave);
        User saved = userRepository.saveAndFlush(toSave);
        log.info("Map a created user entity {} to dto", saved);
        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isEmailExists(String email) {
        log.info("Checking if email: {} already exists in the database", email);
        return userRepository.findByEmail(email)
                             .isPresent();
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDto getById(Long userId) {
        User found = getUserByIdFetchAllCards(userId);
        log.info("Map fetched user entity {} to dto", found);
        return mapper.toDto(found);
    }

    @Transactional
    @Override
    public UserResponseDto update(UserUpdateDto dto, Long userId) {

        User foundById = getUserByIdFetchAllCards(userId);

        User updated;
        if (hasAnyFieldChanged(dto, foundById)) {
            log.info("Check if the provided dto version is not outdated. Dto: [{}] Entity: [{}]", dto, foundById);
            validationUtil.checkIfDtoVersionIsOutdated(foundById.getVersion(), dto);


            String emailToCheck = dto.email();
            log.info("Check if the provided email doesn't exists [{}]", emailToCheck);
            if (!Objects.equals(emailToCheck, foundById.getEmail())) {
                userRepository.findByEmail(emailToCheck)
                              .ifPresent(user -> {
                                  throw new UniqueConstraintViolationException(
                                          String.format("Can't update the email to: [%s]. This email already exists!",
                                                        emailToCheck), HttpStatus.BAD_REQUEST);
                              });
            }

            updated = mapper.updateEntity(dto, foundById);
            log.info("Updated entity {} to save", updated);
            userRepository.saveAndFlush(updated);
        } else {
            log.info("Non of the fields in the dto {} have changed any of the fields in the entity {}", dto, foundById);
            updated = foundById;
        }
        return mapper.toDto(updated);
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        User found = getUserByIdFetchAllCards(userId);
        log.info("Invoking user repository to delete a user: [{}]", found);
        userRepository.delete(found);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserResponseDto> getAllByIds(List<Long> ids) {
        Set<Long> idsToFind = new HashSet<>(ids);
        log.info("Invoking user repository for ids: [{}]", idsToFind);
        List<User> foundUsers = userRepository.findByIdIn(idsToFind);
        log.info("Retrieved users list: {}", foundUsers);

        if (foundUsers.size() != idsToFind.size()) {
            foundUsers.forEach(u -> idsToFind.remove(u.getId()));
            log.info("Haven't found users with ids: {}", idsToFind);
        }
        return foundUsers.stream()
                         .map(mapper::toDto)
                         .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserById(Long id) {
        log.info("Trying to get a user by id: {}", id);
        User found = userRepository.findById(id)
                                   .orElseThrow(
                                           () -> new UserNotFoundException(
                                                   String.format("User with id [%s] doesn't exist", id),
                                                   HttpStatus.NOT_FOUND));
        log.info("Retrieved a user {}", found);
        return found;
    }

    private User getUserByIdFetchAllCards(Long id) {
        log.info("Trying to get a user by id: {} with all cards", id);
        User found = userRepository.findByIdWithAllCards(id)
                                   .orElseThrow(
                                           () -> new UserNotFoundException(
                                                   String.format("User with id [%s] doesn't exist", id),
                                                   HttpStatus.NOT_FOUND));
        log.info("Retrieved a user: {} with all cards {}", found, found.getCards());
        return found;
    }

    private boolean hasAnyFieldChanged(UserUpdateDto d, User e) {
        return !(Objects.equals(d.name(), e.getName()) &&
                Objects.equals(d.surname(), e.getSurname()) &&
                Objects.equals(d.birthDate(), e.getBirthDate()) &&
                Objects.equals(d.email(), e.getEmail())
        );
    }
}
