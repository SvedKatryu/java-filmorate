package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.Validate;

import javax.validation.Valid;


import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> filmStorage = new HashMap<>();
    private int id = 0;
    @PostMapping
    public Film create(@RequestBody @Valid Film film, BindingResult bindingResult) {
        Validate.validate(bindingResult);
        film.setId(++id);
        filmStorage.put(film.getId(), film);
        log.info("Добавили новый фильм");
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film updatedFilm, BindingResult bindingResult) {
        Validate.validate(bindingResult);

        if (filmStorage.containsKey(updatedFilm.getId())) {
            filmStorage.put(updatedFilm.getId(), updatedFilm);
            log.info("Данные фильма изменены");
        } else {
            throw new ValidationException("Данный фильм не найден");
        }
        return updatedFilm;
    }

    @GetMapping
    public List<Film> getAll() {
        return new ArrayList<>(filmStorage.values());
    }
}
