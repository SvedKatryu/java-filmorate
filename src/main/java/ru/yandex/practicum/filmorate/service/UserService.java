package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.db.FriendDbStorage;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final FriendDbStorage friendStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage storage, @Qualifier("friendDbStorage") FriendDbStorage friendStorage) {
        this.userStorage = storage;
        this.friendStorage = friendStorage;
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        userStorage.getUserById(user.getId());
        return userStorage.update(user);
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    public List<User> getCommonFriends(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        return friendStorage.getCommonFriends(user.getId(), friend.getId());
    }

    public void addToFriends(long id, long friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        if (Objects.equals(user.getId(), friend.getId())) {
            throw new NotFoundException("Нельзя добавить себя в друзья");
        }
        try {
            friendStorage.addFriend(user.getId(), friend.getId());
        } catch (NotFoundException e) {
            throw new NotFoundException("Друг с ID:" + friend.getId() + " не добавлен");
        }
    }

    public List<User> getFriends(long userId) {
        User user = userStorage.getUserById(userId);
        log.info("Друзья пользователя с ID: {}", user);
        return friendStorage.getFriends(user.getId());
    }

    public void removeFromFriends(long id, long friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        if (friendStorage.getFriends(user.getId()).contains(friend)) {
            friendStorage.deleteFriend(user.getId(), friend.getId());
        }
        if (friendStorage.getFriends(friend.getId()).contains(user)) {
            friendStorage.deleteFriend(friend.getId(), user.getId());
        }
    }

    public User removeUser(long id) {
        User user = userStorage.getUserById(id);
        userStorage.delete(user);
        return user;
    }
}
