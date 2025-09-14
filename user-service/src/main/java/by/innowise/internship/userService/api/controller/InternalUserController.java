package by.innowise.internship.userService.api.controller;

import by.innowise.common.library.dto.UserProfileDto;
import by.innowise.internship.userService.core.service.facade.InternalUserFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal/users")
@RequiredArgsConstructor
@Slf4j
public class InternalUserController {

    private final InternalUserFacade userFacade;

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getUser(@PathVariable Long userId) {
        log.info("Requested to get user by auth id: {}", userId);
        UserProfileDto found = userFacade.getById(userId);
        log.info("Retrieved user profile: {} by auth id: {}", found, userId);
        return ResponseEntity.ok(found);
    }


}
