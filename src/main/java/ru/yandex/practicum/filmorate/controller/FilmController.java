package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> filmStorage = new HashMap<>();
    private int generatedId;

    private final LocalDate startReleaseDate = LocalDate.of(1895, 12, 28);

    public void validate(Film data) {
        if (data.getReleaseDate().isBefore(startReleaseDate)) {
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

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        validate(film);
        if (filmStorage.containsKey(film.getId())) {
            filmStorage.put(film.getId(), film);
            log.info("Данные пользователя изменены");
        } else {
            throw new ValidationException("Данный фильм не найден");
        }
        return film;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validate(film);
        film.setId(++generatedId);
        filmStorage.put(film.getId(), film);
        log.info("Добавили новый фильм");
        return film;
    }

    @GetMapping
    public List<Film> getAll() {
        log.info("Добавили все фильмы");
        return new ArrayList<>(filmStorage.values());
    }
}
