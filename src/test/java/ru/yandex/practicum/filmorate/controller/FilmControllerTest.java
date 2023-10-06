package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.Validate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {

    final Film film = new Film();



//    @Test
//    void validateFilmFail() {
//        Exception exception = assertThrows(ValidationException.class, () -> filmController.validate(film));
//        assertEquals("Film name invalid", exception.getMessage());
//    }
}
