package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
@Component
public interface LikeStorage {
    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);
}
