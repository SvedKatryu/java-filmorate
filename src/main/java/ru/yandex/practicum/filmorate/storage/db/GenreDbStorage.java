package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getGenreById(long id) {
        String sqlQuery = "select * from genres where id = ?";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, GenreDbStorage::createGenre, id);
        if (genres.size() != 1) {
            throw new DataNotFoundException(String.format("genre with id %s not single", id));
        }
        return genres.get(0);
    }

    @Override
    public List<Genre> getAll() {
        String sqlQuery = "select * from genres";
        return jdbcTemplate.query(sqlQuery, GenreDbStorage::createGenre);

    }

    public void addGenreToFilm(Long filmId, Set<Genre> genres) {
        String sqlQuery = "insert into film_genres (film_id, genre_id) values (?, ?);";
        jdbcTemplate.batchUpdate(
                sqlQuery,
                genres,
                10,
                (PreparedStatement ps, Genre g) -> {
                    ps.setLong(1, filmId);
                    ps.setLong(2, g.getId());
                }
        );
    }

    public List<Genre> getGenreFilms(Long filmId) {
        String sqlQuery = "select * from film_genres " +
                "left join genres on genres.genre_id = film_genres.genre_id " +
                "where film_id = ? " +
                "order by genres.genre_id";
        return jdbcTemplate.query(sqlQuery, GenreDbStorage::createGenre, filmId);
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
