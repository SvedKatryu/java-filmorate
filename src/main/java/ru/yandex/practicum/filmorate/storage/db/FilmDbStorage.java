package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    private static final LocalDate START_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private void validate(Film data) {
        if (data.getReleaseDate().isBefore(START_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза некорректна");
        }
        if (data.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (data.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (data.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

    @Override
    public Film getFilmById(long id) {
        String sqlQuery = "select * from mpa where id = ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, FilmDbStorage::queryRowMapperFilm, id);
        if (films.size() != 1) {
            throw new DataNotFoundException(String.format("mpa with id %s not single", id));
        }
        return films.get(0);
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "select * from users";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::queryRowMapperFilm);
    }

    @Override
    public Film create(Film film) {
        validate(film);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        Map<String, Object> films = newMap(film);
        Long id = simpleJdbcInsert.executeAndReturnKey(films).longValue();
        film.setId(id);

        addGenreToFilm(film);

        genreStorage.addGenreToFilm(film.getId(), film.getGenres());

        if (film.getMpa().getId() != 0) {
            film.setMpa(mpaFromTable(film.getMpa().getId()));
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        validate(film);
        Film checkFilm = getFilmById(film.getId());
        String sqlQuery = "update films set " +
                "name = ?, description = ?, release_date = ? , duration = ? , mpa_id = ? where film_id = ?";
        jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        genreStorage.deleteGenreFilmsByFilmId(film.getId());

        addGenreToFilm(film);

        genreStorage.addGenreToFilm(film.getId(), film.getGenres());

        return film;
    }

    private Map<String, Object> newMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("mpa_id", film.getMpa().getId());
        return values;
    }

    private void addGenreToFilm(Film film) {
        for (Genre genre : film.getGenres()) {
            genre.setName(genreStorage.getGenreById(genre.getId()).getName());
        }
    }

    private Mpa mpaFromTable(Long id) {
        String sqlQuery = "select * from mpa where mpa_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, FilmDbStorage::createMpa, id);
    }

    static Mpa createMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }

    @Override
    public Film delete(Film film) {
        Film checkFilm = getFilmById(film.getId());
        String sqlQuery = "delete from films where film_id = ?";
        jdbcTemplate.update(sqlQuery, checkFilm.getId());
        return film;
    }

    static Film queryRowMapperFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpaId(rs.getInt("mpa_id"))
                .build();
    }
}
