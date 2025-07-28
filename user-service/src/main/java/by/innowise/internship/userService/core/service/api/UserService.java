package by.innowise.internship.userService.core.service.api;

import by.innowise.internship.userService.api.dto.user.UserCreateDto;
import by.innowise.internship.userService.api.dto.user.UserResponseDto;
import by.innowise.internship.userService.api.dto.user.UserUpdateDto;

import java.util.List;

public interface UserService {

    UserResponseDto create(UserCreateDto dto);

    boolean isEmailExists(String email);

    UserResponseDto getById(Long userId);

    UserResponseDto update(UserUpdateDto dto, Long userId);

    void delete(Long userId);

    List<UserResponseDto> getAllByIds(List<Long> ids);
}
