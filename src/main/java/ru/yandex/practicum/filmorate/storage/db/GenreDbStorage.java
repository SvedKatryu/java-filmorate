package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getGenreById(long id) {
        String sqlQuery = "select * from genres where genre_id = ?";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, GenreDbStorage::createGenre, id);
        if (genres.size() > 1) {
            throw new DataNotFoundException(String.format("genre with id %s not single", id));
        }
        if (genres.isEmpty()) {
            throw new NotFoundException(String.format("film with id %s not found", id));
        }
        return genres.get(0);
    }

    @Override
    public List<Genre> getAll() {
        String sqlQuery = "select * from genres";
        return jdbcTemplate.query(sqlQuery, GenreDbStorage::createGenre);

    }

    public void addGenreToFilm(Long filmId, List<Genre> genres) {
        String sqlQuery = "insert into film_genres (film_id, genre_id) values (?, ?);";
        jdbcTemplate.batchUpdate(
                sqlQuery,
                genres,
                20,
                (PreparedStatement ps, Genre g) -> {
                    ps.setLong(1, filmId);
                    ps.setLong(2, g.getId());
                }
        );
    }

    public void getWithGenreFilms(List<Film> films) {
        String inSql = String.join(", ", Collections.nCopies(films.size(), "?"));
        String sqlQuery = "select * from genres g, film_genres fg where fg.genre_id = g.genre_id and fg.film_id IN (" + inSql + ")";
        Map<Long, Film> mapFilmsById = films.stream().collect(toMap(Film::getId, identity()));
        jdbcTemplate.query(
                sqlQuery,
                (rs) -> {
                    final long filmId = rs.getLong("film_id");
                    Film film = mapFilmsById.get(filmId);
                    if (film != null) {
                        Genre genre = createGenre(rs, 0);
                        List<Genre> genres = new ArrayList<>();
                        if (film.getGenres() != null) {
                            genres = film.getGenres();
                        }
                        genres.add(genre);
                        film.setGenres(genres);
                    }
                },
                mapFilmsById.keySet().toArray()
        );
    }

    public void deleteGenreFilmsByFilmId(Long filmId) {
        String sqlQuery = "delete from film_genres where film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    static Genre createGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("name"))
                .build();
    }
}
