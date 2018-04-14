package com.breuninger.boot.validation.web;

import static java.util.Collections.singletonList;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class ErrorHalRepresentationFactoryTest {

  private ResourceBundleMessageSource messageSource;

  @Before
  public void setUp() {
    messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename("ValidationMessages");
    messageSource.setUseCodeAsDefaultMessage(true);
  }

  @Test
  public void shouldBuildRepresentationForValidationResults() {
    // given
    final var factory = new ErrorHalRepresentationFactory(messageSource, new ObjectMapper());

    // when
    final var mockErrors = mock(Errors.class);
    final var fieldError = new FieldError("someObject", "xyzField", "rejected", true, new String[]{"NotEmpty"}, new Object[]{},
      "Some default message");
    when(mockErrors.getAllErrors()).thenReturn(singletonList(fieldError));
    when(mockErrors.getErrorCount()).thenReturn(1);
    final var errorHalRepresentation = factory.halRepresentationForValidationErrors(mockErrors);

    // then
    assertThat(errorHalRepresentation.getErrorMessage(), is("Validation failed. 1 error(s)"));
    final var listOfViolations = errorHalRepresentation.getErrors().get("xyzField");
    assertThat(listOfViolations, hasSize(1));
    assertThat(listOfViolations.get(0), hasEntry("key", "text.not_empty"));
    assertThat(listOfViolations.get(0), hasEntry("message", "Some default message"));
    assertThat(listOfViolations.get(0), hasEntry("rejected", "rejected"));
  }

  @Test
  public void shouldNotCrashOnNullValues() {
    // given
    final var factory = new ErrorHalRepresentationFactory(messageSource, new ObjectMapper());

    // when
    final var mockErrors = mock(Errors.class);
    final var fieldError = new FieldError("someObject", "xyzField", null, true, new String[]{"NotEmpty"}, new Object[]{},
      "Some default message");
    when(mockErrors.getAllErrors()).thenReturn(singletonList(fieldError));
    when(mockErrors.getErrorCount()).thenReturn(1);
    final var errorHalRepresentation = factory.halRepresentationForValidationErrors(mockErrors);

    // then
    assertThat(errorHalRepresentation.getErrorMessage(), is("Validation failed. 1 error(s)"));
    final var listOfViolations = errorHalRepresentation.getErrors().get("xyzField");
    assertThat(listOfViolations, hasSize(1));
    assertThat(listOfViolations.get(0), hasEntry("key", "text.not_empty"));
    assertThat(listOfViolations.get(0), hasEntry("message", "Some default message"));
    assertThat(listOfViolations.get(0), hasEntry("rejected", "null"));
  }
}
