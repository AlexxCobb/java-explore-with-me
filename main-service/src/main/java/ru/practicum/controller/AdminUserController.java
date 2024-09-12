package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.UserParam;
import ru.practicum.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.info("Поступил POST-запрос на добавление user");
        return userService.createUser(userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Поступил DELETE-запрос на удаление user");
        userService.deleteUser(userId);
    }

    @GetMapping
    public List<UserDto> getUsers(@Valid UserParam userParam) {
        log.info("Поступил GET-запрос на получение users c параметрами ids = {}, from - {}, size - {}", userParam.getUserIds(), userParam.getFrom(), userParam.getSize());
        return userService.getUsers(userParam);

        //@RequestParam(required = false) List<Long> userIds,
        //                                  @RequestParam(defaultValue = "0") Integer from,
        //                                  @RequestParam(defaultValue = "10") Integer size
    }
}
