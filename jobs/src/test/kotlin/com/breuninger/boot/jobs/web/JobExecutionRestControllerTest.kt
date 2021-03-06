package com.breuninger.boot.jobs.web

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.service.JobExecutionService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class JobExecutionRestControllerTest {

  private val jobExecutionService = mockk<JobExecutionService>()
  private val jobExecutionRestController = JobExecutionRestController(jobExecutionService)

  @Test
  fun `ensure find calls JobExecutionService findOne and returns its result`() {
    val jobExecutionIdString = "foo"
    val jobExecutionId = JobExecutionId(jobExecutionIdString)
    val jobExecution = JobExecution(jobExecutionId, JobId("bar"))
    every { jobExecutionService.findOne(jobExecutionId) } returns jobExecution

    assertThat(jobExecutionRestController.find(jobExecutionIdString)).isEqualTo(jobExecution)
    verify { jobExecutionService.findOne(jobExecutionId) }
  }

  @Test
  fun `ensure create calls JobExecutionService create and returns its result`() {
    val jobIdValue = "foo"
    val jobId = JobId(jobIdValue)
    val job = Job(jobId)
    every { jobExecutionService.create(jobId) } returns job

    assertThat(jobExecutionRestController.create(jobIdValue)).isEqualTo(job)
    verify { jobExecutionService.create(jobId) }
  }
}
