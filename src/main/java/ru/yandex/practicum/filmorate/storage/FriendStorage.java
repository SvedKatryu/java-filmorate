package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface FriendStorage {

    void addFriend(Long userId, Long friendId);
    void deleteFriend(Long userId, Long friendId);

    List<User> getFriends(Long userId);
    List<User> getCommonFriends(Long userId, Long friendId);
}

