package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
@Deprecated
@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private long id = 0;

    private final Map<Long, User> users = new HashMap<>();

    private Long getNextId() {
        return ++id;
    }

    private void validate(User data) {
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

    @Override
    public User update(User user) {
        validate(user);
        if (users.containsKey(user.getId())) {
            user.setFriends(users.get(user.getId()).getFriends());

            users.put(user.getId(), user);
            log.info("Данные пользователя изменены");
        } else {
            throw new NotFoundException("Данный пользователь не найден");
        }
        return user;
    }

    @Override
    public User create(User user) {
        validate(user);
        user.setId(getNextId());
        user.setFriends(new HashSet<>());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Добавили нового пользователя", user);
        return user;
    }

    @Override
    public List<User> getAll() {
        log.info("Добавили всех пользователей");
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        return users.get(id);
    }
}
