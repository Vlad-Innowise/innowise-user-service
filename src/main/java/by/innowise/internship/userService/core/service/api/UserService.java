package by.innowise.internship.userService.core.service.api;

import by.innowise.internship.userService.api.dto.user.UserCreateDto;
import by.innowise.internship.userService.api.dto.user.UserResponseDto;

public interface UserService {

    UserResponseDto create(UserCreateDto dto);

    boolean isEmailExists(String email);

    UserResponseDto getById(Long userId);
}
