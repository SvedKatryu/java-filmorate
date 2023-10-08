package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")

public class UserController {

    private final Map<Integer, User> userStorage = new HashMap<>();
    private int generatedId;

    public void validate(User data) {
        if (data.getEmail().isBlank() || !data.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (data.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (LocalDate.now().isBefore(data.getBirthday())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    @PutMapping
    public User update(@Valid @RequestBody User updatedUser) {
        validate(updatedUser);
        if (userStorage.containsKey(updatedUser.getId())) {
            userStorage.put(updatedUser.getId(), updatedUser);
            log.info("Данные пользователя изменены");
        } else {
            throw new ValidationException("Данный пользователь не найден");
        }
        return updatedUser;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(++generatedId);
        userStorage.put(user.getId(), user);
        log.info("Добавили нового пользователя", user);
        return user;
    }

    @GetMapping
    public List<User> getAll() {
        log.info("Добавили всех пользователей");
        return new ArrayList<>(userStorage.values());
    }

}
