package com.breuninger.boot.status.configuration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

import static com.breuninger.boot.status.domain.Status.OK;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.breuninger.boot.status.domain.ApplicationStatus;
import com.breuninger.boot.status.indicator.ApplicationStatusAggregator;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
  SystemInfoConfiguration.class,
  ClusterInfoConfiguration.class,
  ApplicationInfoConfiguration.class,
  VersionInfoConfiguration.class,
  TeamInfoConfiguration.class,
  ApplicationStatusAggregatorConfiguration.class
})
public class ApplicationStatusAggregatorConfigurationTest {

  @Autowired
  private ApplicationStatusAggregator applicationStatusAggregator;

  private ApplicationStatus status;

  @Before
  public void setUp() {
    status = applicationStatusAggregator.aggregatedStatus();
  }

  @Test
  public void checkOverallStatus() {
    assertThat(status.status, is(OK));
    assertThat(status.application, is(notNullValue()));
    assertThat(status.cluster, is(notNullValue()));
    assertThat(status.system, is(notNullValue()));
    assertThat(status.vcs, is(notNullValue()));
    assertThat(status.team, is(notNullValue()));
    assertThat(status.statusDetails.isEmpty(), is(true));
  }
}
