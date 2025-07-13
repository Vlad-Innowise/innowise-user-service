package by.innowise.internship.userService.core.service.impl;

import by.innowise.internship.userService.core.exception.UserNotFoundException;
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

    @Transactional(readOnly = true)
    @Override
    public boolean isEmailExists(String email) {
        log.info("Checking if email: {} already exists in the database", email);
        return userRepository.findByEmail(email)
                             .isPresent();
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
}
