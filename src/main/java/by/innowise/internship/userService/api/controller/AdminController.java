package by.innowise.internship.userService.api.controller;

import by.innowise.internship.userService.api.dto.user.UserResponseDto;
import by.innowise.internship.userService.core.service.api.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getUsersByIds(@RequestParam("id") List<Long> ids) {
        log.info("Requested to retrieve users with ids: {}", ids);
        List<UserResponseDto> foundUsers = userService.getAllByIds(ids);
        log.info("Retrieved found users list: {}", foundUsers);
        return ResponseEntity.ok(foundUsers);
    }

}
