package by.innowise.internship.userService.api.controller;

import by.innowise.internship.userService.api.dto.user.UserCreateDto;
import by.innowise.internship.userService.api.dto.user.UserResponseDto;
import by.innowise.internship.userService.api.dto.user.UserUpdateDto;
import by.innowise.internship.userService.core.service.api.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody @Valid UserCreateDto dto) {
        log.info("Requested to create a user: {}", dto);
        UserResponseDto created = userService.create(dto);
        log.info("Created a new user: {}", created);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable @Positive Long userId) {
        UserResponseDto found = userService.getById(userId);
        log.info("Retrieved user: {} by userId: {}", found, userId);
        return ResponseEntity.ok(found);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> update(@RequestBody @Valid UserUpdateDto dto,
                                                  @PathVariable @Positive Long userId) {
        log.info("Requested to update a user: {}", dto);
        UserResponseDto responseDto = userService.update(dto, userId);
        log.info("Updated a user: {}", responseDto);
        return ResponseEntity.ok(responseDto);
    }
}
