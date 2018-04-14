package com.breuninger.boot.jobs.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.breuninger.boot.jobs.definition.DefaultJobDefinition.cronJobDefinition;

import java.time.Instant;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.breuninger.boot.jobs.definition.JobDefinition;
import com.breuninger.boot.jobs.repository.JobMetaRepository;

public class MetaJobRunnableTest {

  private JobMetaRepository metaRepository;
  private MetaJobRunnable testee;

  @Before
  public void setUp() {
    metaRepository = mock(JobMetaRepository.class);
    testee = new MetaJobRunnable("someJob", metaRepository) {
      @Override
      public JobDefinition getJobDefinition() {
        return cronJobDefinition("someJob", "Some Job", "", "* * * * *", 0, Optional.empty());
      }

      @Override
      public boolean execute() {
        return true;
      }
    };
  }

  @Test
  public void shouldGetJobType() {
    assertThat(testee.getJobType(), is("someJob"));
  }

  @Test
  public void shouldDeleteMeta() {
    testee.deleteMeta("key");
    verify(metaRepository).setValue("someJob", "key", null);
  }

  @Test
  public void shouldSetMetaAsString() {
    testee.setMeta("key", "value");
    verify(metaRepository).setValue("someJob", "key", "value");
  }

  @Test
  public void shouldSetMetaAsInt() {
    testee.setMeta("key", 42);
    verify(metaRepository).setValue("someJob", "key", "42");
  }

  @Test
  public void shouldSetMetaAsLong() {
    testee.setMeta("key", 42L);
    verify(metaRepository).setValue("someJob", "key", "42");
  }

  @Test
  public void shouldSetMetaAsInstant() {
    testee.setMeta("key", Instant.ofEpochMilli(42));
    verify(metaRepository).setValue("someJob", "key", "42");
  }

  @Test
  public void shouldGetJobMetaAsString() {
    when(metaRepository.getValue("someJob", "key")).thenReturn("42");
    final var value = testee.getMetaAsString("key");
    assertThat(value, is("42"));
  }

  @Test
  public void shouldGetJobMetaAsInt() {
    when(metaRepository.getValue("someJob", "key")).thenReturn("42");
    final var value = testee.getMetaAsInt("key", -1);
    assertThat(value, is(42));
  }

  @Test
  public void shouldGetJobMetaAsLong() {
    when(metaRepository.getValue("someJob", "key")).thenReturn("42");
    final var value = testee.getMetaAsLong("key", -1L);
    assertThat(value, is(42L));
  }

  @Test
  public void shouldGetJobMetaAsInstant() {
    when(metaRepository.getValue("someJob", "key")).thenReturn("42");
    final var value = testee.getMetaAsInstant("key");
    assertThat(value.toEpochMilli(), is(42L));
  }
}
