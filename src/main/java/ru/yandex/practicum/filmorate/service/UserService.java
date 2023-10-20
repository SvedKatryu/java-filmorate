package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        Set<Long> currentUsers = userStorage.getUserById(id).getFriends();
        Set<Long> otherUsers = userStorage.getUserById(otherId).getFriends();

        Set<Long> commonFriendsId = currentUsers.stream()
                .filter(otherUsers::contains)
                .collect(Collectors.toSet());

        List<User> commonFriends = new ArrayList<>();
        for (long userId : commonFriendsId) {
            commonFriends.add(userStorage.getUserById(userId));
        }

        return commonFriends;
    }

    public void addToFriends(long id, long friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    public List<User> getFriends(long id) {
        User user = userStorage.getUserById(id);
        List<User> friends = new ArrayList<>();
        for (long friendId : user.getFriends()) {
            friends.add(userStorage.getUserById(friendId));
        }
        return friends;
    }

    public void removeFromFriends(long id, long friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }
}
