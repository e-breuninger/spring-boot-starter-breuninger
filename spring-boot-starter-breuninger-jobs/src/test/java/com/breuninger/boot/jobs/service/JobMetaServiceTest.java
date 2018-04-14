package com.breuninger.boot.jobs.service;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.breuninger.boot.jobs.domain.JobMeta;
import com.breuninger.boot.jobs.domain.RunningJob;
import com.breuninger.boot.jobs.repository.JobBlockedException;
import com.breuninger.boot.jobs.repository.JobMetaRepository;

public class JobMetaServiceTest {

  @Mock
  private JobMetaRepository jobMetaRepository;
  @Mock
  private JobMutexGroups jobMutexGroups;

  private JobMetaService jobMetaService;

  @Before
  public void setUp() {
    initMocks(this);
    when(jobMutexGroups.mutexJobTypesFor(anyString())).thenReturn(emptySet());
    jobMetaService = new JobMetaService(jobMetaRepository, jobMutexGroups);
  }

  @Test
  public void shouldAquireRunLock() {
    when(jobMetaRepository.findAllJobTypes()).thenReturn(emptySet());
    when(jobMetaRepository.setRunningJob("myJobType", "jobId")).thenReturn(true);
    when(jobMetaRepository.getJobMeta("myJobType")).thenReturn(new JobMeta("myJobType", false, false, "", emptyMap()));

    jobMetaService.aquireRunLock("jobId", "myJobType");

    verify(jobMetaRepository).setRunningJob("myJobType", "jobId");
  }

  @Test
  public void shouldReleaseRunLock() {
    jobMetaService.releaseRunLock("someType");
    verify(jobMetaRepository).clearRunningJob("someType");
  }

  @Test(expected = JobBlockedException.class)
  public void shouldNotAquireLockIfAlreadyRunning() {
    // given
    when(jobMetaRepository.findAllJobTypes()).thenReturn(emptySet());
    when(jobMetaRepository.getJobMeta("myJobType")).thenReturn(new JobMeta("myJobType", true, false, "", emptyMap()));
    when(jobMetaRepository.setRunningJob("myJobType", "someId")).thenReturn(false);

    // when
    jobMetaService.aquireRunLock("jobId", "myJobType");
  }

  @Test(expected = JobBlockedException.class)
  public void shouldNotStartJobIfBlockedByAnotherJob() {
    // given
    when(jobMetaRepository.findAllJobTypes()).thenReturn(new HashSet<>(asList("job1", "job2")));
    when(jobMetaRepository.getRunningJob("job1")).thenReturn("42");
    when(jobMetaRepository.getJobMeta("job2")).thenReturn(new JobMeta("job2", false, false, "", emptyMap()));
    when(jobMetaRepository.setRunningJob("job2", "first")).thenReturn(true);
    when(jobMutexGroups.mutexJobTypesFor("job2")).thenReturn(new HashSet<>(asList("job1", "job2")));

    // when
    try {
      jobMetaService.aquireRunLock("first", "job2");
    }

    // then
    catch (final JobBlockedException e) {
      verify(jobMetaRepository).clearRunningJob("job2");
      throw e;
    }
  }

  @Test
  public void shouldReturnRunningJobsDocument() {
    when(jobMetaRepository.findAllJobTypes()).thenReturn(new HashSet<>(asList("someType", "someOtherType")));
    when(jobMetaRepository.getRunningJob("someType")).thenReturn("someId");
    when(jobMetaRepository.getRunningJob("someOtherType")).thenReturn("someOtherId");

    assertThat(jobMetaService.runningJobs(),
      containsInAnyOrder(new RunningJob("someId", "someType"), new RunningJob("someOtherId", "someOtherType")));
  }

  @Test(expected = JobBlockedException.class)
  public void shouldNotStartADisabledJob() {
    // given
    when(jobMetaRepository.findAllJobTypes()).thenReturn(singleton("jobType"));
    when(jobMetaRepository.getJobMeta("jobType")).thenReturn(new JobMeta("jobType", false, true, "", emptyMap()));

    // when
    try {
      jobMetaService.aquireRunLock("someId", "jobType");
    }

    // then
    catch (final JobBlockedException e) {
      assertThat(e.getMessage(), is("Job 'jobType' is currently disabled"));
      throw e;
    }
  }

  @Test
  public void shouldDisableJobType() {
    jobMetaService.disable("jobType", null);
    verify(jobMetaRepository).disable("jobType", null);
  }

  @Test
  public void shouldDisableJobTypeWithComment() {
    jobMetaService.disable("jobType", "some comment");
    verify(jobMetaRepository).disable("jobType", "some comment");
  }

  @Test
  public void shouldEnableJobType() {
    jobMetaService.enable("jobType");
    verify(jobMetaRepository).enable("jobType");
  }
}
