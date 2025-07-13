package by.innowise.internship.userService.core.service.impl;

import by.innowise.internship.userService.api.dto.user.UserCreateDto;
import by.innowise.internship.userService.api.dto.user.UserResponseDto;
import by.innowise.internship.userService.core.exception.UserNotFoundException;
import by.innowise.internship.userService.core.mapper.UserMapper;
import by.innowise.internship.userService.core.repository.UserRepository;
import by.innowise.internship.userService.core.repository.entity.User;
import by.innowise.internship.userService.core.service.api.InternalUserService;
import by.innowise.internship.userService.core.service.api.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, InternalUserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

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
}
