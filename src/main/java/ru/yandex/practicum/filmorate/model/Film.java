package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
@SuperBuilder
@NotNull
@NoArgsConstructor
public class Film {

    private long id;
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @NotNull
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;
    private Set<Long> likes;
    private int rate;

    private Mpa mpa;
    @NotNull
    private int mpaId;
    //private String mpaName;
    //private List<String> genre = new ArrayList<>();
//    private LinkedHashSet<Genre> genres = new LinkedHashSet<>();
    private List<Genre> genres = new ArrayList<>();
}
