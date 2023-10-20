package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

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

    @PutMapping
    public Film update(@Valid @RequestBody Film updatedFilm) {
        validate(updatedFilm);
        return filmService.update(updatedFilm);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validate(film);
        return filmService.create(film);
    }

    @GetMapping
    public List<Film> getAll() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable(value = "id") int filmId) {
        return filmService.getFilmById(filmId);
    }

    @PutMapping("{id}/like/{userId}")
    public ResponseEntity<HttpStatus> addLike(@PathVariable(value = "id") int filmId,
                                              @PathVariable(value = "userId") int userId) {
        filmService.addLike(filmId, userId);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<HttpStatus> removeLike(@PathVariable(value = "id") int filmId,
                                                 @PathVariable(value = "userId") int userId) {
        filmService.removeLike(filmId, userId);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        return filmService.getMostPopularFilms(count);
    }
}
