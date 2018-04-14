package com.breuninger.boot.validation.web;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.ResponseEntity.unprocessableEntity;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ValidationExceptionHandler {

  private static final MediaType APPLICATION_HAL_JSON_ERROR = MediaType.parseMediaType(
    "application/hal+json; " + "profiles=\"http://spec.breuninger.de/profiles/error\"; charset=utf-8");
  private final ErrorHalRepresentationFactory errorHalRepresentationFactory;

  public ValidationExceptionHandler(final ErrorHalRepresentationFactory errorHalRepresentationFactory) {
    this.errorHalRepresentationFactory = errorHalRepresentationFactory;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(UNPROCESSABLE_ENTITY)
  public ResponseEntity<ErrorHalRepresentation> handleException(final MethodArgumentNotValidException exception) {
    return unprocessableEntity().contentType(APPLICATION_HAL_JSON_ERROR)
      .body(errorHalRepresentationFactory.halRepresentationForValidationErrors(exception.getBindingResult()));
  }
}
