package by.innowise.internship.userService.core.service.facade;

import by.innowise.common.library.dto.UserProfileDto;
import by.innowise.internship.userService.core.mapper.UserMapper;
import by.innowise.internship.userService.core.repository.entity.User;
import by.innowise.internship.userService.core.service.api.InternalUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InternalUserFacadeImpl implements InternalUserFacade {

    private final InternalUserService internalUserService;
    private final UserMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public UserProfileDto getById(Long userId) {
        log.info("Getting user entity by user auth id: {}", userId);
        User user = internalUserService.getUserByAuthId(userId);
        return mapper.toProfileDto(user);
    }
}
