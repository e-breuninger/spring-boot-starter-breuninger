package com.breuninger.boot.validation.validators;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class InstantValidator implements ConstraintValidator<IsInstant, String> {

  @Override
  public void initialize(final IsInstant constraintAnnotation) {
  }

  @Override
  public boolean isValid(final String value, final ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    try {
      Instant.parse(value);
    } catch (final DateTimeParseException e) {
      return false;
    }
    return true;
  }
}
