package com.breuninger.boot.validation.validators;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<IsEnum, String> {

  private Set<String> availableEnumNames;
  private boolean ignoreCase;
  private boolean allowNull;

  @Override
  public void initialize(final IsEnum annotation) {
    final Class<? extends Enum<?>> enumClass = annotation.enumClass();
    availableEnumNames = Stream.of(enumClass.getEnumConstants()).map(Enum::name).collect(Collectors.toSet());
    ignoreCase = annotation.ignoreCase();
    allowNull = annotation.allowNull();
  }

  @Override
  public boolean isValid(final String value, final ConstraintValidatorContext context) {
    if (value == null) {
      return allowNull;
    } else {
      return availableEnumNames.stream().anyMatch(o -> {
        if (ignoreCase) {
          return o.equalsIgnoreCase(value);
        } else {
          return o.equals(value);
        }
      });
    }
  }
}
