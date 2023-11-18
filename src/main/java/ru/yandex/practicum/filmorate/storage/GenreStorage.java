package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

@Component
public interface GenreStorage {
    List<Genre> getAll();

    Genre getGenreById(long id);
    List<Genre> getGenreFilms(Long filmId);

    void deleteGenreFilmsByFilmId(Long filmId);

    void addGenreToFilm(Long filmId, Set<Genre> genres);
}
