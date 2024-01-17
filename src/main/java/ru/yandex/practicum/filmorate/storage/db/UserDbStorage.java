package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@AllArgsConstructor
@Qualifier("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

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
    public User getUserById(long id) {
        String sqlQuery = "select * from users where user_id = ?";
        List<User> users = jdbcTemplate.query(sqlQuery, UserDbStorage::RowMapperUser, id);
        if (users.size() > 1) {
            throw new DataNotFoundException(String.format("user with id %s not single", id));
        }
        if (users.isEmpty()) {
            throw new NotFoundException(String.format("user with id %s not found", id));
        }
        return users.get(0);
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "select * from users";
        return jdbcTemplate.query(sqlQuery, UserDbStorage::RowMapperUser);
    }

    @Override
    public User delete(User user) {
        User checkUser = getUserById(user.getId());
        String sqlQuery = "delete from users where user_id = ?";
        jdbcTemplate.update(sqlQuery, checkUser.getId());
        return user;
    }

    @Override
    public User create(User user) {
        validate(user);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        Long id = simpleJdbcInsert.executeAndReturnKey(newMap(user)).longValue();
        user.setId(id);
        log.info("Добавили нового пользователя: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        validate(user);
        User checkUser = getUserById(user.getId());
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(
                sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    private Map<String, Object> newMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        if (!Objects.equals(user.getName(), "")) {
            values.put("name", user.getName());
        } else {
            values.put("name", user.getLogin());
        }
        values.put("birthday", user.getBirthday());
        return values;
    }

    static User RowMapperUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();

    }
}
