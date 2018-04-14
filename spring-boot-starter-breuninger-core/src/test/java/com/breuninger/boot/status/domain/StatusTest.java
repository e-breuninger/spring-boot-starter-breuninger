package com.breuninger.boot.status.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static com.breuninger.boot.status.domain.Status.ERROR;
import static com.breuninger.boot.status.domain.Status.OK;
import static com.breuninger.boot.status.domain.Status.WARNING;
import static com.breuninger.boot.status.domain.Status.plus;

import org.junit.Test;

public class StatusTest {

  @Test
  public void shouldBeOk() {
    assertThat(plus(OK, OK), is(OK));
  }

  @Test
  public void shouldBeWarning() {
    assertThat(plus(OK, WARNING), is(WARNING));
  }

  @Test
  public void shouldBeError() {
    assertThat(plus(WARNING, ERROR), is(ERROR));
  }

  @Test
  public void shouldBeAbleToConcatenate() {
    assertThat(plus(OK, plus(WARNING, OK)), is(WARNING));
  }
}
