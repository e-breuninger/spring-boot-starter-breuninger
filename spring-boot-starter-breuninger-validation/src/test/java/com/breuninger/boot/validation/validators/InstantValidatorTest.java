package com.breuninger.boot.validation.validators;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class InstantValidatorTest {

  @Test
  public void shouldReturnFalseIfInstantIsNotParseable() {
    // given
    final var subject = new InstantValidator();
    // when
    final var result = subject.isValid("blabla", null);
    // then
    assertThat(result, is(false));
  }

  @Test
  public void shouldReturnTrueIfInstantIsParseable() {
    // given
    final var subject = new InstantValidator();
    // when
    final var result = subject.isValid("2042-02-05T10:17:38.858Z", null);
    // then
    assertThat(result, is(true));
  }

  @Test
  public void shouldReturnTrueIfInstantIsNull() {
    // given
    final var subject = new InstantValidator();
    // when
    final var result = subject.isValid(null, null);
    // then
    assertThat(result, is(true));
  }
}
