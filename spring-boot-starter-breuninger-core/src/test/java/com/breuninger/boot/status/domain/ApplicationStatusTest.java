package com.breuninger.boot.status.domain;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

import static com.breuninger.boot.status.domain.ApplicationStatus.applicationStatus;
import static com.breuninger.boot.status.domain.Status.ERROR;
import static com.breuninger.boot.status.domain.Status.OK;
import static com.breuninger.boot.status.domain.Status.WARNING;
import static com.breuninger.boot.status.domain.StatusDetail.statusDetail;

import org.junit.Test;

public class ApplicationStatusTest {

  @Test
  public void shouldHaveStatusOkIfDetailsAreOk() {
    // given
    final var applicationStatus = applicationStatus(mock(ApplicationInfo.class), null, mock(SystemInfo.class),
      mock(VersionInfo.class), mock(TeamInfo.class), singletonList(statusDetail("bar", OK, "a message")));
    // then
    assertThat(applicationStatus.status, is(OK));
  }

  @Test
  public void shouldHaveStatusWarningIfDetailsContainWarnings() {
    // given
    final var applicationStatus = applicationStatus(mock(ApplicationInfo.class), null, mock(SystemInfo.class),
      mock(VersionInfo.class), mock(TeamInfo.class),
      asList(statusDetail("bar", OK, "a message"), statusDetail("foobar", WARNING, "another message")));
    // then
    assertThat(applicationStatus.status, is(WARNING));
  }

  @Test
  public void shouldHaveStatusErrorIfDetailsContainWarnings() {
    // given
    final var applicationStatus = applicationStatus(mock(ApplicationInfo.class), null, mock(SystemInfo.class),
      mock(VersionInfo.class), mock(TeamInfo.class),
      asList(statusDetail("bar", OK, "a message"), statusDetail("foobar", ERROR, "another message"),
        statusDetail("foobar", WARNING, "yet another message")));
    // then
    assertThat(applicationStatus.status, is(ERROR));
  }
}
