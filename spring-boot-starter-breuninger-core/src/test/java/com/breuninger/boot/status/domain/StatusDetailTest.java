package com.breuninger.boot.status.domain;

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

public class StatusDetailTest {

  @Test
  public void shouldHaveAdditionalAttributes() {
    // given
    final var statusDetail = statusDetail("foo", ERROR, "message", singletonMap("foo", "bar"));
    // when
    final var theMap = statusDetail.getDetails();
    // then
    assertThat(theMap, hasEntry("foo", "bar"));
  }

  @Test
  public void shouldResultInWarning() {
    // given
    var statusDetail = statusDetail("foo", OK, "message", singletonMap("foo", "bar"));
    // when
    statusDetail = statusDetail.toWarning("different message");
    // then
    assertThat(statusDetail.getName(), is("foo"));
    assertThat(statusDetail.getMessage(), is("different message"));
    assertThat(statusDetail.getStatus(), is(WARNING));
    assertThat(statusDetail.getDetails(), hasEntry("foo", "bar"));
  }

  @Test
  public void shouldResultInError() {
    // given
    var statusDetail = statusDetail("foo", WARNING, "message", singletonMap("foo", "bar"));
    // when
    statusDetail = statusDetail.toError("different message");
    // then
    assertThat(statusDetail.getName(), is("foo"));
    assertThat(statusDetail.getMessage(), is("different message"));
    assertThat(statusDetail.getStatus(), is(ERROR));
    assertThat(statusDetail.getDetails(), hasEntry("foo", "bar"));
  }

  @Test
  public void shouldResultInOk() {
    // given
    var statusDetail = statusDetail("foo", WARNING, "message", singletonMap("foo", "bar"));
    // when
    statusDetail = statusDetail.toOk("different message");
    // then
    assertThat(statusDetail.getName(), is("foo"));
    assertThat(statusDetail.getMessage(), is("different message"));
    assertThat(statusDetail.getStatus(), is(OK));
    assertThat(statusDetail.getDetails(), hasEntry("foo", "bar"));
  }

  @Test
  public void shouldAddDetail() {
    // given
    var statusDetail = statusDetail("foo", WARNING, "message", singletonMap("foo", "bar"));
    // when
    statusDetail = statusDetail.withDetail("bar", "baz");
    // then
    assertThat(statusDetail.getName(), is("foo"));
    assertThat(statusDetail.getMessage(), is("message"));
    assertThat(statusDetail.getStatus(), is(WARNING));
    assertThat(statusDetail.getDetails(), hasEntry("foo", "bar"));
    assertThat(statusDetail.getDetails(), hasEntry("bar", "baz"));
  }

  @Test
  public void shouldRemoveDetail() {
    // given
    var statusDetail = statusDetail("foo", WARNING, "message", singletonMap("foo", "bar"));
    // when
    statusDetail = statusDetail.withoutDetail("foo");
    // then
    assertThat(statusDetail.getName(), is("foo"));
    assertThat(statusDetail.getMessage(), is("message"));
    assertThat(statusDetail.getStatus(), is(WARNING));
    assertThat(statusDetail.getDetails(), not(hasEntry("foo", "bar")));
  }

  @Test
  public void shouldOverwriteDetail() {
    // given
    var statusDetail = statusDetail("foo", WARNING, "message", singletonMap("foo", "bar"));
    // when
    statusDetail = statusDetail.withDetail("foo", "baz");
    // then
    assertThat(statusDetail.getName(), is("foo"));
    assertThat(statusDetail.getMessage(), is("message"));
    assertThat(statusDetail.getStatus(), is(WARNING));
    assertThat(statusDetail.getDetails(), hasEntry("foo", "baz"));
  }
}
