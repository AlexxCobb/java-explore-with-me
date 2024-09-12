package ru.practicum.dto.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.PARAMETER, FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = {DateValidation.class})
public @interface ValidDate {
    String message() default "Дата должна соответствовать форме: \"yyyy-MM-dd HH:mm:ss\"";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
