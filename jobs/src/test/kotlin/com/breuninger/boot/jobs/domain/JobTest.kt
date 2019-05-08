package com.breuninger.boot.jobs.domain

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test

internal class JobTest {

  @Test
  fun `ensure isRunning returns true`() {
    val job = createJob()

    assertThat(job.isRunning()).isTrue()
  }

  @Test
  fun `ensure isRunning returns false`() {
    val job = createJob(null)

    assertThat(job.isRunning()).isFalse()
  }

  @Test
  fun `ensure isNotRunning returns false`() {
    val job = createJob()

    assertThat(job.isNotRunning()).isFalse()
  }

  @Test
  fun `ensure isNotRunning returns true`() {
    val job = createJob(null)

    assertThat(job.isNotRunning()).isTrue()
  }

  private fun createJob(runningJobExecutionId: JobExecutionId? = JobExecutionId("bar")) =
    Job(JobId("foo"), runningJobExecutionId, false, "", emptyMap())
}
