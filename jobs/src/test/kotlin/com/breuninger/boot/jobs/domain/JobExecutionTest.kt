package com.breuninger.boot.jobs.domain

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.time.Instant

internal class JobExecutionTest {

  @Test
  fun `ensure hasStopped returns true correctly`() {
    val jobExecution = createJobExecution(stopped = Instant.now())

    assert(jobExecution.hasStopped())
  }

  @Test
  fun `ensure hasStopped returns false correctly`() {
    val jobExecution = createJobExecution()

    assertFalse(jobExecution.hasStopped())
  }

  @Test
  fun `ensure hasNotStopped returns false correctly`() {
    val jobExecution = createJobExecution(stopped = Instant.now())

    assertFalse(jobExecution.hasNotStopped())
  }

  @Test
  fun `ensure hasNotStopped returns true correctly`() {
    val jobExecution = createJobExecution()

    assert(jobExecution.hasNotStopped())
  }

  private fun createJobExecution(stopped: Instant? = null) =
    JobExecution(JobExecutionId("foo"), JobId("bar"), JobExecution.Status.OK, Instant.now(), stopped, emptyList(),
      "foobar", Instant.now())
}
