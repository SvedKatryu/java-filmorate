package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    List<Genre> getAll();

    Genre getGenreById(long id);

    List<Genre> getGenreFilms(Long filmId);

    void deleteGenreFilmsByFilmId(Long filmId);

    void addGenreToFilm(Long filmId, List<Genre> genres);
}
