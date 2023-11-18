package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       LikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
    }

//    public FilmService(LikeStorage likeStorage) {
//        this.likeStorage = likeStorage;
//    }

//    public FilmService(FilmStorage filmStorage,
//                       UserStorage userStorage) {
//        this.filmStorage = filmStorage;
//        this.userStorage = userStorage;
//    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film getFilmById(long filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public void addLike(long filmId, long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        log.info("Поставлен лайк фильму: {}", filmStorage.getFilmById(film.getId()));
        filmStorage.getFilmById(film.getId()).getLikes().add(user.getId());
        likeStorage.addLike(film.getId(), user.getId());
    }

    public void removeLike(long filmId, long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        if (film.getLikes().contains(user.getId())) {
            log.info("Удалён лайк фильму: {}", film);
            film.getLikes().remove(user.getId());
            likeStorage.deleteLike(film.getId(), user.getId());
        }
    }

    public List<Film> getMostPopularFilms(long count) {
        List<Film> allFilms = filmStorage.getAll();

        return allFilms.stream()
                .sorted(Comparator.comparingInt(Film::getRate).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
