package by.innowise.internship.userService.core.service.api;

import by.innowise.internship.userService.api.dto.user.UserCreateDto;
import by.innowise.internship.userService.api.dto.user.UserResponseDto;
import by.innowise.internship.userService.api.dto.user.UserUpdateDto;

import java.util.List;

public interface UserService {

    UserResponseDto create(UserCreateDto dto, Long authUserId);

    boolean isEmailExists(String email);

    UserResponseDto getById(Long authUserId);

    UserResponseDto update(UserUpdateDto dto, Long authUserId);

    void delete(Long authUserId);

    List<UserResponseDto> getAllByIds(List<Long> authUserIds);
}
