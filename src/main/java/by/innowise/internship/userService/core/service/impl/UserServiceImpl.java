package by.innowise.internship.userService.core.service.impl;

import by.innowise.internship.userService.core.repository.UserRepository;
import by.innowise.internship.userService.core.service.api.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public boolean isEmailExists(String email) {
        log.info("Checking if email: {} already exists in the database", email);
        return userRepository.findByEmail(email)
                             .isPresent();
    }
}
