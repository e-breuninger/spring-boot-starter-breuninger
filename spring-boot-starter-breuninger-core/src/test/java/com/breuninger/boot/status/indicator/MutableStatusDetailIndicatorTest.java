package com.breuninger.boot.status.indicator;

import static java.util.Collections.singletonMap;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

import static com.breuninger.boot.status.domain.Status.ERROR;
import static com.breuninger.boot.status.domain.Status.OK;
import static com.breuninger.boot.status.domain.Status.WARNING;
import static com.breuninger.boot.status.domain.StatusDetail.statusDetail;

import org.junit.Test;

public class MutableStatusDetailIndicatorTest {

  @Test
  public void shouldIndicateInitialStatus() {
    // given
    final var indicator = new MutableStatusDetailIndicator(statusDetail("foo", OK, "message"));
    // then
    assertThat(indicator.statusDetail().getName(), is("foo"));
    assertThat(indicator.statusDetail().getStatus(), is(OK));
    assertThat(indicator.statusDetail().getMessage(), is("message"));
  }

  @Test
  public void shouldIndicateUpdatedStatus() {
    // given
    final var initial = statusDetail("foo", ERROR, "message");
    final var indicator = new MutableStatusDetailIndicator(initial);
    // when
    indicator.update(initial.toOk("ok now"));
    // then
    assertThat(indicator.statusDetail().getName(), is("foo"));
    assertThat(indicator.statusDetail().getMessage(), is("ok now"));
    assertThat(indicator.statusDetail().getStatus(), is(OK));
  }

  @Test
  public void shouldIndicateOkStatus() {
    // given
    final var initial = statusDetail("foo", ERROR, "message");
    final var indicator = new MutableStatusDetailIndicator(initial);
    // when
    indicator.toOk("ok now");
    // then
    assertThat(indicator.statusDetail().getName(), is("foo"));
    assertThat(indicator.statusDetail().getMessage(), is("ok now"));
    assertThat(indicator.statusDetail().getStatus(), is(OK));
  }

  @Test
  public void shouldIndicateWarnStatus() {
    // given
    final var initial = statusDetail("foo", ERROR, "message");
    final var indicator = new MutableStatusDetailIndicator(initial);
    // when
    indicator.toWarning("something strange");
    // then
    assertThat(indicator.statusDetail().getName(), is("foo"));
    assertThat(indicator.statusDetail().getMessage(), is("something strange"));
    assertThat(indicator.statusDetail().getStatus(), is(WARNING));
  }

  @Test
  public void shouldIndicateErrorStatus() {
    // given
    final var initial = statusDetail("foo", WARNING, "message");
    final var indicator = new MutableStatusDetailIndicator(initial);
    // when
    indicator.toError("broken");
    // then
    assertThat(indicator.statusDetail().getName(), is("foo"));
    assertThat(indicator.statusDetail().getMessage(), is("broken"));
    assertThat(indicator.statusDetail().getStatus(), is(ERROR));
  }

  @Test
  public void shouldIndicateAdditionalDetails() {
    // given
    final var initial = statusDetail("foo", WARNING, "message");
    final var indicator = new MutableStatusDetailIndicator(initial);
    // when
    indicator.withDetail("foo", "bar");
    // then
    assertThat(indicator.statusDetail().getName(), is("foo"));
    assertThat(indicator.statusDetail().getMessage(), is("message"));
    assertThat(indicator.statusDetail().getStatus(), is(WARNING));
    assertThat(indicator.statusDetail().getDetails(), hasEntry("foo", "bar"));
  }

  @Test
  public void shouldDeleteAdditionalDetail() {
    // given
    final var initial = statusDetail("foo", WARNING, "message", singletonMap("foo", "baz"));
    final var indicator = new MutableStatusDetailIndicator(initial);
    // when
    indicator.withoutDetail("bar");
    // then
    assertThat(indicator.statusDetail().getName(), is("foo"));
    assertThat(indicator.statusDetail().getMessage(), is("message"));
    assertThat(indicator.statusDetail().getStatus(), is(WARNING));
    assertThat(indicator.statusDetail().getDetails(), not(hasEntry("bar", "baz")));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailToUpdateStatusWithDifferentName() {
    // given
    final var indicator = new MutableStatusDetailIndicator(statusDetail("foo", OK, "message"));
    // when
    indicator.update(statusDetail("bar", OK, "message"));
    // then an exception is thrown
  }
}
