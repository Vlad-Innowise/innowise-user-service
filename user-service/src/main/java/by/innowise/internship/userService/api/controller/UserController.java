package by.innowise.internship.userService.api.controller;

import by.innowise.internship.security.dto.UserHolder;
import by.innowise.internship.userService.api.dto.user.UserCreateDto;
import by.innowise.internship.userService.api.dto.user.UserResponseDto;
import by.innowise.internship.userService.api.dto.user.UserUpdateDto;
import by.innowise.internship.userService.core.service.api.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody @Valid UserCreateDto dto,
                                                  @AuthenticationPrincipal UserHolder userHolder) {
        Long authId = userHolder.crossServiceUserId();
        log.info("Requested to create a user profile: {} for auth id: {}", dto, authId);
        UserResponseDto created = userService.create(dto, authId);
        log.info("Created a new user profile: {}", created);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<UserResponseDto> getById(@AuthenticationPrincipal UserHolder userHolder) {
        Long authId = userHolder.crossServiceUserId();
        log.info("Requested to get user by auth id: {}", authId);
        UserResponseDto found = userService.getById(authId);
        log.info("Retrieved user profile: {} by auth id: {}", found, authId);
        return ResponseEntity.ok(found);
    }

    @PutMapping
    public ResponseEntity<UserResponseDto> update(@RequestBody @Valid UserUpdateDto dto,
                                                  @AuthenticationPrincipal UserHolder userHolder) {
        Long authId = userHolder.crossServiceUserId();
        log.info("Requested to update a user profile: {} for auth id: {}", dto, authId);
        UserResponseDto responseDto = userService.update(dto, authId);
        log.info("Updated a user profile: {}", responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserHolder userHolder) {
        Long authId = userHolder.crossServiceUserId();
        log.info("Requested to delete a user profile by auth id: [{}] ", authId);
        userService.delete(authId);
        log.info("User with auth id: [{}] successfully deleted", authId);
        return ResponseEntity.ok()
                             .build();
    }
}
