package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    public void addFriend(Long userId, Long friendId) {
        String sqlQuery = "insert into friendships (user_id, friends_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        String sqlQuery = "delete from friendships where user_id = ? and friends_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        String sqlQuery = "select u.* " +
                "from USERS u, FRIENDSHIPS f " +
                "where u.USER_ID = f.FRIENDS_ID " +
                "AND f.USER_ID = ?";
        return jdbcTemplate.query(sqlQuery, FriendDbStorage::mapRowFriends, userId);
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        String sqlQuery = "select u.* from USERS u, FRIENDSHIPS f, FRIENDSHIPS o " +
                "where u.USER_ID = f.FRIENDS_ID " +
                "AND u.USER_ID = o.FRIENDS_ID " +
                "AND f.USER_ID = ? " +
                "AND o.USER_ID = ?";
        return jdbcTemplate.query(sqlQuery, FriendDbStorage::mapRowFriends, userId, friendId);
    }

    static User mapRowFriends(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}
