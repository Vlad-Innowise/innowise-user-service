package by.innowise.internship.userService.core.util.mapper;

import by.innowise.internship.userService.core.repository.entity.User;
import by.innowise.internship.userService.core.service.api.InternalUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CardInfoMapperHelper {

    private final InternalUserService internalUserService;

    @Named("mapUserById")
    public User mapUserById(Long id) {
        User found = internalUserService.getById(id);
        log.info("Mapping a user with id: {} to card entity", id);
        return found;
    }

}
