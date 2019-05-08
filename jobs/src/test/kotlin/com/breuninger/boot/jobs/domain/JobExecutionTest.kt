package com.breuninger.boot.jobs.domain

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.breuninger.boot.jobs.domain.JobExecution.Status.OK
import org.junit.jupiter.api.Test
import java.time.Instant

internal class JobExecutionTest {

  @Test
  fun `ensure hasStopped returns true correctly`() {
    val jobExecution = createJobExecution(stopped = Instant.now())

    assertThat(jobExecution.hasStopped()).isTrue()
  }

  @Test
  fun `ensure hasStopped returns false correctly`() {
    val jobExecution = createJobExecution()

    assertThat(jobExecution.hasStopped()).isFalse()
  }

  @Test
  fun `ensure hasNotStopped returns false correctly`() {
    val jobExecution = createJobExecution(stopped = Instant.now())

    assertThat(jobExecution.hasNotStopped()).isFalse()
  }

  @Test
  fun `ensure hasNotStopped returns true correctly`() {
    val jobExecution = createJobExecution()

    assertThat(jobExecution.hasNotStopped()).isTrue()
  }

  private fun createJobExecution(stopped: Instant? = null) = JobExecution(
    JobExecutionId("foo"),
    JobId("bar"),
    OK,
    Instant.now(),
    stopped,
    emptyList(),
    "foobar",
    Instant.now()
  )
}
