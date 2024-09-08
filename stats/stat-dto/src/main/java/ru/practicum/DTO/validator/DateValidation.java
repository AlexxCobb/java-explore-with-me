package ru.practicum.DTO.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateValidation implements ConstraintValidator<ValidDate, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(value);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}
