package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
@Deprecated
public class InMemoryFilmStorage implements FilmStorage {

    private long id = 0;

    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate START_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private Long getNextId() {
        return ++id;
    }

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
    public Film update(Film film) {
        validate(film);
        if (films.containsKey(film.getId())) {
            film.setLikes(films.get(film.getId()).getLikes());
            updateFilmRate(film);

            films.put(film.getId(), film);
            log.info("Параметры фильма изменены");
        } else {
            throw new NotFoundException("Данный фильм не найден");
        }
        return film;
    }

    @Override
    public Film create(Film film) {
        validate(film);
        film.setId(getNextId());
        film.setLikes(new HashSet<>());
        updateFilmRate(film);

        films.put(film.getId(), film);
        log.info("Добавили новый фильм");
        return film;
    }

    @Override
    public List<Film> getAll() {
        log.info("Добавили все фильмы");
        return new ArrayList<>(films.values());
    }

    private void updateFilmRate(Film film) {
        film.setRate(film.getLikes().size());
    }

    @Override
    public Film getFilmById(long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм не найден");
        }
        return films.get(id);
    }

    @Override
    public Film delete(Film film) {
        if (films.containsKey(film.getId())) {
            films.remove(film.getId(), film);
            return film;
        }
        throw new NotFoundException("Фильма с ID: " + film.getId() + " нет");
    }
}
