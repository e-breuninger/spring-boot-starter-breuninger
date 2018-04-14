package com.breuninger.boot.validation.validators;

import java.lang.annotation.Annotation;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SafeIdValidator implements ConstraintValidator<Annotation, String> {

  private static final Pattern IdPattern = Pattern.compile("[a-zA-Z0-9\\-_]*");

  @Override
  public void initialize(final Annotation annotation) {
  }

  @Override
  public boolean isValid(final String id, final ConstraintValidatorContext context) {
    if (id == null) {
      return true;
    }
    return IdPattern.matcher(id).matches();
  }
}
