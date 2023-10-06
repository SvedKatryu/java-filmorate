package ru.yandex.practicum.filmorate.validation;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.List;

public class Validate {
    public static void validate(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder message = new StringBuilder();
            List<FieldError> err = bindingResult.getFieldErrors();
            for (FieldError e : err) {
                message.append(e.getField())
                        .append(" - ")
                        .append(e.getDefaultMessage());
            }
            throw new ValidationException(message.toString());
        }
    }
}