package by.innowise.internship.userService.core.service.facade;

import by.innowise.common.library.dto.UserProfileDto;

public interface InternalUserFacade {

    UserProfileDto getById(Long userId);

}
