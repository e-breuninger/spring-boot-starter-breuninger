package com.breuninger.boot.jobs.configuration;

import static java.time.Duration.ofSeconds;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.of;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.breuninger.boot.jobs.definition.DefaultJobDefinition.fixedDelayJobDefinition;
import static com.breuninger.boot.status.domain.Status.OK;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.breuninger.boot.configuration.BreuningerApplicationProperties;
import com.breuninger.boot.jobs.definition.DefaultJobDefinition;
import com.breuninger.boot.jobs.definition.JobDefinition;
import com.breuninger.boot.jobs.service.JobDefinitionService;
import com.breuninger.boot.jobs.status.JobStatusCalculator;
import com.breuninger.boot.status.indicator.CompositeStatusDetailIndicator;

public class JobsConfigurationTest {

  private BreuningerApplicationProperties properties;
  private JobsConfiguration testee;

  @Before
  public void setUp() {
    properties = new BreuningerApplicationProperties();
    testee = new JobsConfiguration(new JobsProperties(), properties);
  }

  @Test
  public void shouldIndicateOkIfNoJobDefinitionsAvailable() {
    // given
    final var defaultCalculator = mock(JobStatusCalculator.class);
    when(defaultCalculator.getKey()).thenReturn("warningOnLastJobFailed");

    final var noJobDefinitions = mock(JobDefinitionService.class);
    when(noJobDefinitions.getJobDefinitions()).thenReturn(emptyList());

    // when
    final var statusDetail = testee.jobStatusDetailIndicator(noJobDefinitions, singletonList(defaultCalculator)).statusDetail();
    // then
    assertThat(statusDetail.getStatus(), is(OK));
    assertThat(statusDetail.getName(), is("Jobs"));
    assertThat(statusDetail.getMessage(), is("No job definitions configured in application."));
  }

  @Test
  public void shouldConstructCompositeStatusDetailIndicator() {
    // given
    final var defaultCalculator = mock(JobStatusCalculator.class);
    when(defaultCalculator.getKey()).thenReturn("warningOnLastJobFailed");

    // when
    final var indicator = testee.jobStatusDetailIndicator(someJobDefinitionService(), singletonList(defaultCalculator));
    // then
    assertThat(indicator, is(instanceOf(CompositeStatusDetailIndicator.class)));
  }

  @Test
  public void shouldUseDefaultJobStatusDetailIndicator() {
    // given
    final var defaultCalculator = mock(JobStatusCalculator.class);
    when(defaultCalculator.getKey()).thenReturn("warningOnLastJobFailed");

    // when
    testee.jobStatusDetailIndicator(someJobDefinitionService(), singletonList(defaultCalculator)).statusDetail();

    // then
    verify(defaultCalculator).statusDetail(any(JobDefinition.class));
  }

  @Test
  public void shouldUseConfiguredJobStatusDetailIndicator() {
    // given
    final var jobsProperties = new JobsProperties();
    jobsProperties.getStatus().setCalculator(new HashMap() {{
      put("test", "errorOnLastJobFailed");
    }});

    testee = new JobsConfiguration(jobsProperties, properties);

    final var defaultCalculator = mock(JobStatusCalculator.class);
    when(defaultCalculator.getKey()).thenReturn("warningOnLastJobFailed");

    final var testCalculator = mock(JobStatusCalculator.class);
    when(testCalculator.getKey()).thenReturn("errorOnLastJobFailed");

    // when
    testee.jobStatusDetailIndicator(someJobDefinitionService(), asList(defaultCalculator, testCalculator)).statusDetail();

    // then
    verify(testCalculator).statusDetail(any(JobDefinition.class));
  }

  @Test
  public void shouldNormalizeJobTypesInConfiguredJobStatusDetailIndicators() {
    // given
    final var jobsProperties = new JobsProperties();
    jobsProperties.getStatus().setCalculator(new HashMap() {{
      put("soMe-TeSt job", "errorOnLastJobFailed");
    }});

    testee = new JobsConfiguration(jobsProperties, properties);

    final var defaultCalculator = mock(JobStatusCalculator.class);
    when(defaultCalculator.getKey()).thenReturn("warningOnLastJobFailed");

    final var testCalculator = mock(JobStatusCalculator.class);
    when(testCalculator.getKey()).thenReturn("errorOnLastJobFailed");

    final var jobDefinitionService = mock(JobDefinitionService.class);
    when(jobDefinitionService.getJobDefinitions()).thenReturn(singletonList(someJobDefinition("Some Test Job")));

    // when
    testee.jobStatusDetailIndicator(jobDefinitionService, asList(defaultCalculator, testCalculator)).statusDetail();

    // then
    verify(testCalculator).statusDetail(any(JobDefinition.class));
  }

  private JobDefinitionService someJobDefinitionService() {
    final var jobDefinitionService = mock(JobDefinitionService.class);
    when(jobDefinitionService.getJobDefinitions()).thenReturn(singletonList(someJobDefinition("test")));
    return jobDefinitionService;
  }

  private DefaultJobDefinition someJobDefinition(final String jobType) {
    return fixedDelayJobDefinition(jobType, "test", "", ofSeconds(10), 0, of(ofSeconds(10)));
  }
}
