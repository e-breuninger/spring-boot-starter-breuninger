package com.breuninger.boot.validation.web;

import static java.util.Comparator.comparing;

import java.util.Locale;

import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ErrorHalRepresentationFactory {

  private final ResourceBundleMessageSource messageSource;
  private final ObjectMapper objectMapper;

  public ErrorHalRepresentationFactory(final ResourceBundleMessageSource breuningerValidationMessageSource,
                                       final ObjectMapper objectMapper) {
    messageSource = breuningerValidationMessageSource;
    this.objectMapper = objectMapper;
  }

  public ErrorHalRepresentation halRepresentationForValidationErrors(final Errors validationResult) {
    final var builder = ErrorHalRepresentation.builder()
      .withErrorMessage(String.format("Validation failed. %d error(s)", validationResult.getErrorCount()));

    validationResult.getAllErrors()
      .stream()
      .filter(o -> o instanceof FieldError)
      .map(FieldError.class::cast)
      .sorted(comparing(FieldError::getField))
      .forEach(
        e -> builder.withError(e.getField(), messageSource.getMessage(e.getCode() + ".key", null, "unknown", Locale.getDefault()),
          e.getDefaultMessage(), serializeRejectedValue(e)));

    return builder.build();
  }

  private String serializeRejectedValue(final FieldError e) {
    if (e.getRejectedValue() instanceof String) {
      return (String) e.getRejectedValue();
    } else {
      try {
        return objectMapper.writeValueAsString(e.getRejectedValue());
      } catch (final JsonProcessingException ignore) {
        return e.getRejectedValue().toString();
      }
    }
  }
}
