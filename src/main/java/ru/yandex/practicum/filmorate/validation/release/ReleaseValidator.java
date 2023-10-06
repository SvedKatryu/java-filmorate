package ru.yandex.practicum.filmorate.validation.release;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReleaseValidator implements ConstraintValidator<ReleaseAnnotation, LocalDate> {
    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext context) {
        return localDate.isAfter(LocalDate.of(1895, 12, 28));
    }
}

