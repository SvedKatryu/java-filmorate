package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.Validate;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> userStorage = new HashMap<>();
    private int id = 0;

    @PostMapping
    public User create(@RequestBody @Valid User user, BindingResult bindingResult) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        Validate.validate(bindingResult);
        user.setId(++id);
        userStorage.put(user.getId(), user);
        log.info("Добавили нового пользователя");
        return user;
    }

    @PutMapping
    public User update(@RequestBody @Valid User updatedUser, BindingResult bindingResult) {
        Validate.validate(bindingResult);

        if (userStorage.containsKey(updatedUser.getId())) {
            userStorage.put(updatedUser.getId(), updatedUser);
            log.info("Данные пользователя изменены");
        } else {
            throw new ValidationException("Данный пользователь не найден");
        }
        return updatedUser;
    }

    @GetMapping
    public List<User> getAll() {
        return new ArrayList<>(userStorage.values());
    }
}
