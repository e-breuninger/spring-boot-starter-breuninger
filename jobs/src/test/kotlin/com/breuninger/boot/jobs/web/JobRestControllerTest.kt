package com.breuninger.boot.jobs.web

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.service.JobService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

internal class JobRestControllerTest{

  val jobService = mockk<JobService>()
  val jobRestController = JobRestController(jobService)

  @Test
  fun `ensure update calls JobService updateDisableState and returns its result`(){
    val jobIdString = "foo"
    val jobId = JobId(jobIdString)
    val job = Job(jobId)
    val jobModified = job.copy(disabled = true, disableComment = "foo bar")

    every { jobService.updateDisableState(jobId, job) } returns jobModified
    assertThat(jobRestController.update(jobIdString, job)).isEqualTo(jobModified)
    verify { jobService.updateDisableState(jobId, job) }
  }
}
