package ru.practicum.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateValidation implements ConstraintValidator<ValidDate, String> {

    static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(value);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}