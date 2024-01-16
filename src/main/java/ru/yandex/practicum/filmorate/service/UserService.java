package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.db.FriendDbStorage;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final FriendDbStorage friendStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage storage,  @Qualifier("friendDbStorage") FriendDbStorage friendStorage) {
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
//        if (user.getFriends().contains(friend.getId())) {
//            throw new NotFoundException("Друг с ID:" + friend.getId() + " уже добавлен");
//        }
        log.info("Добавлен в друзья: {}", friend);
        Set<Long> friends;
        if (user.getFriends() == null) {
            friends = new java.util.HashSet<>(Collections.emptySet());
        } else {
            friends = user.getFriends();
        }
        friends.add(friend.getId());
        user.setFriends(friends);
        log.info("ПриветПривет: {}", user);
        try {
            friendStorage.addFriend(user.getId(), friend.getId());
        } catch (NotFoundException e) {
            throw new NotFoundException("Друг с ID:" + friend.getId() + " не добавлен");
        }
    }

    public List<User> getFriends(long userId) {
        User user = userStorage.getUserById(userId);
        log.info("Друзья пользователя с ID: {}", user);
//        return user.getFriends(user.getId());
        return friendStorage.getFriends(user.getId());
    }

    public void removeFromFriends(long id, long friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        if (user.getFriends() != null && user.getFriends().contains(friend.getId())) {
            user.getFriends().remove(friendId);
        }
//        if (friend.getFriends() != null && friend.getFriends().contains(user.getId())) {
//            friend.getFriends().remove(user.getId());
//        }
    }
}
