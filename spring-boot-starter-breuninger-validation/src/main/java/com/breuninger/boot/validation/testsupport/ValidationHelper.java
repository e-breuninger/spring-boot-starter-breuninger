package com.breuninger.boot.validation.testsupport;

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;

public class ValidationHelper {
  private static final Validator validator;

  static {
    final var resourceBundleLocator = new PlatformResourceBundleLocator(
      ResourceBundleMessageInterpolator.USER_VALIDATION_MESSAGES, null, true);
    final var messageInterpolator = new ResourceBundleMessageInterpolator(resourceBundleLocator);
    validator = Validation.byDefaultProvider()
      .configure()
      .messageInterpolator(messageInterpolator)
      .buildValidatorFactory()
      .getValidator();
  }

  public static <T> List<String> getViolatedFields(final T apiRepresentation) {
    final var violations = validator.validate(apiRepresentation);
    return violations.stream().map(ConstraintViolation::getPropertyPath).map(Path::toString).collect(toList());
  }
}
