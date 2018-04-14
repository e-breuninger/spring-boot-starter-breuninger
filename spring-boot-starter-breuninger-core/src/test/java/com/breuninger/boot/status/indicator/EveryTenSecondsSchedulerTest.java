package com.breuninger.boot.status.indicator;

import static java.util.Collections.singletonList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.breuninger.boot.status.domain.ApplicationStatus.applicationStatus;
import static com.breuninger.boot.status.domain.StatusDetail.statusDetail;

import org.junit.Test;

import com.breuninger.boot.status.domain.ApplicationInfo;
import com.breuninger.boot.status.domain.ApplicationStatus;
import com.breuninger.boot.status.domain.Status;
import com.breuninger.boot.status.domain.SystemInfo;
import com.breuninger.boot.status.domain.TeamInfo;
import com.breuninger.boot.status.domain.VersionInfo;
import com.breuninger.boot.status.scheduler.EveryTenSecondsScheduler;
import com.breuninger.boot.status.scheduler.Scheduler;

public class EveryTenSecondsSchedulerTest {

  private static final ApplicationStatus SOME_STATUS = applicationStatus(mock(ApplicationInfo.class), null, mock(SystemInfo.class),
    mock(VersionInfo.class), mock(TeamInfo.class), singletonList(statusDetail("test", Status.OK, "everything is fine")));
  private static final ApplicationStatus SOME_OTHER_STATUS = applicationStatus(mock(ApplicationInfo.class), null,
    mock(SystemInfo.class), mock(VersionInfo.class), mock(TeamInfo.class),
    singletonList(statusDetail("test", Status.ERROR, "some error")));

  @Test
  public void shouldDelegateStatusAggregation() {
    final var statusAggregator = mock(ApplicationStatusAggregator.class);
    when(statusAggregator.aggregatedStatus()).thenReturn(SOME_STATUS);

    final Scheduler scheduler = new EveryTenSecondsScheduler(statusAggregator);
    scheduler.update();
    assertThat(statusAggregator.aggregatedStatus(), is(SOME_STATUS));
  }

  @Test
  public void shouldUpdateStatus() {
    final var statusAggregator = mock(ApplicationStatusAggregator.class);
    when(statusAggregator.aggregatedStatus()).thenReturn(SOME_STATUS).thenReturn(SOME_OTHER_STATUS);

    final Scheduler scheduler = new EveryTenSecondsScheduler(statusAggregator);
    // when
    scheduler.update();
    // then
    assertThat(statusAggregator.aggregatedStatus(), is(SOME_STATUS));
    // when
    scheduler.update();
    // then
    assertThat(statusAggregator.aggregatedStatus(), is(SOME_OTHER_STATUS));
  }
}
