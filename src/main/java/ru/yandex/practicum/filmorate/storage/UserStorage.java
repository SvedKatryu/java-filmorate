package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface UserStorage {

    List<User> getAll();

    User create(User user);

    User update(User user);

    User getUserById(long id);

    User delete(User user);

}
