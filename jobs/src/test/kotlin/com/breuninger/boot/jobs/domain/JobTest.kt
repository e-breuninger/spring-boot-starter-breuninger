package com.breuninger.boot.jobs.domain

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

internal class JobTest {

  private fun createJob(runningJobExecutionId: JobExecutionId? = JobExecutionId("bar")) = Job(JobId("foo"), runningJobExecutionId, false,"" ,emptyMap())

  @Test
    fun `ensure isRunning returns true`() {
      val job = createJob()
      assert(job.isRunning())
    }

  @Test
  fun `ensure isRunning returns false`() {
    val job = createJob(null)
    assertFalse(job.isRunning())
  }

  @Test
  fun `ensure isNotRunning returns false`() {
    val job = createJob()
    assertFalse(job.isNotRunning())
  }

  @Test
  fun `ensure isNotRunning returns true`() {
    val job = createJob(null)
    assert(job.isNotRunning())
  }
}
