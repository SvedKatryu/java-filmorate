package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public List<Genre> getAll() {
        log.info("Получен запрос GET /genres");
        return genreStorage.getAll();
    }

    public Genre getGenreById(long id) {
        Genre genre = genreStorage.getGenreById(id);
        log.info("Жанр с ID: {}", genre);
        return genre;
    }
}
