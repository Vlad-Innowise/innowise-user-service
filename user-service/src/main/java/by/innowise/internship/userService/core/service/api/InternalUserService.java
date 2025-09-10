package by.innowise.internship.userService.core.service.api;

import by.innowise.internship.userService.core.repository.entity.User;

public interface InternalUserService {

    User getUserByAuthId(Long authId);

}
