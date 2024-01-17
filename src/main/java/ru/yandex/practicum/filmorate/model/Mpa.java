package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@SuperBuilder
@NotNull
@NoArgsConstructor
public class Mpa {
    private long id;
    @NotBlank
    private String name;
    private String description;
}
