package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@AllArgsConstructor
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
        String sqlQuery = "SELECT f.*, m.NAME as mpa_name FROM FILMS f JOIN MPA M on f.mpa_id  = M.mpa_id  WHERE f.film_id =?";
        List<Film> films = jdbcTemplate.query(sqlQuery, FilmDbStorage::rowNewMapperFilm, id);
        if (films.size() > 1) {
            throw new DataNotFoundException(String.format("film with id %s not single", id));
        }
        if (films.isEmpty()) {
            throw new NotFoundException(String.format("film with id %s not found", id));
        }
        Film film = films.get(0);

        List<Genre> genres = new ArrayList<>();
        String userSql = "SELECT user_id FROM LIKES WHERE film_id =?";
        SqlRowSet set = jdbcTemplate.queryForRowSet(userSql, film.getId());

        while (set.next()) {
            Set<Long> likes = new HashSet<>();
            if (film.getLikes() != null) {
                likes = film.getLikes();
            }
            likes.add(set.getLong("user_id"));
            film.setLikes(likes);
            film.setRate(likes.size());
        }
        genres = genreStorage.getGenreFilms(id);
        film.setGenres(genres);
        return film;
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

    private void addGenreToFilm(Film film) {
        List<Genre> genres = film.getGenres();
        Set<Genre> genresSet = new LinkedHashSet<>(genres);
        List<Genre> genresArr = new ArrayList<Genre>(genresSet);
        film.setGenres(genresArr);
        for (Genre genre : film.getGenres()) {
            genre.setName(genreStorage.getGenreById(genre.getId()).getName());
        }
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

    private Mpa mpaFromTable(Long id) {
        String sqlQuery = "select * from mpa where mpa_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, FilmDbStorage::createMpa, id);
    }

    static Mpa createMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getLong("mpa_id"))
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

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT f.*, m.NAME as mpa_name FROM FILMS f JOIN MPA m on f.mpa_id  = m.mpa_id";
        List<Film> films = jdbcTemplate.query(sqlQuery, FilmDbStorage::rowNewMapperFilm);
        List<Genre> genres = new ArrayList<>();
        for (Film film : films) {
            String userSql = "SELECT user_id FROM LIKES WHERE film_id =?";
            SqlRowSet set = jdbcTemplate.queryForRowSet(userSql, film.getId());

            while (set.next()) {
                Set<Long> likes = new HashSet<>();
                if (film.getLikes() != null) {
                    likes = film.getLikes();
                }
                likes.add(set.getLong("user_id"));
                film.setLikes(likes);
                film.setRate(likes.size());
            }
            genres = genreStorage.getGenreFilms(film.getId());
            film.setGenres(genres);
        }
        return films;
    }

    static Film rowNewMapperFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa_id"));
        mpa.setName(rs.getString("mpa_name"));
        film.setMpa(mpa);
        return film;
    }
}
