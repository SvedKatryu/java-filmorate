package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashMap;
import java.util.List;

public interface GenreStorage {
    List<Genre> getAll();

    Genre getGenreById(long id);

    void getWithGenreFilms(List<Film> films);

    void deleteGenreFilmsByFilmId(Long filmId);

    void addGenreToFilm(Long filmId, List<Genre> genres);
}
