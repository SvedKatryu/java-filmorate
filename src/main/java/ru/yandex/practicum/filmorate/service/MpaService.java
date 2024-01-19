package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.db.MpaDbStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaService {

    private final MpaDbStorage mpaDbStorage;

    public List<Mpa> getAll() {
        log.info("Получен запрос GET /mpa");
        return mpaDbStorage.getAll();
    }

    public Mpa getMpaById(long id) {
        Mpa mpa = mpaDbStorage.getMpaById(id);
        log.info("Mpa с ID: {}", mpa);
        return mpa;
    }
}
