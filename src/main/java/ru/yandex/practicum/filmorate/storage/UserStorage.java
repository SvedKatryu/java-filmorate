package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

public interface UserStorage {

    List<User> getAll();

    User create(User user);

    User update(User user);

    User getUserById(long id);

}
