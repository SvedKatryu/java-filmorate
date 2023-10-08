package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.*;
import java.time.LocalDate;
@Data
@SuperBuilder
@NotNull
@NoArgsConstructor
public class User {
    private int id;
    @NotNull
    @NotBlank(message = "Электронная почта не может быть пустой и должна содержать символ @")
    @Email(message = "Электронная почта не может быть пустой и должна содержать символ @")
    private String email;
    @NotNull
    @NotEmpty(message = "Логин не может быть пустым и содержать пробелы")
    @Pattern(regexp = "\\S+",  message = "Логин не может быть пустым и содержать пробелы")
    private String login;
    private String name;
    @NotNull
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
