package com.breuninger.boot.jobs.service

import com.breuninger.boot.jobs.actuator.health.JobHealthIndicator
import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobExecutionMessage
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.repository.JobExecutionRepository
import com.breuninger.boot.jobs.repository.JobExecutorRegistry
import com.breuninger.boot.jobs.repository.JobRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.Instant

internal class JobExecutionServiceTest {

  private val jobExecutionRepository = mockk<JobExecutionRepository>()
  private val jobRepository = mockk<JobRepository>()
  private val healthIndicator = mockk<JobHealthIndicator>()
  private val jobExecutorRegistry = mockk<JobExecutorRegistry>()
  private val jobExecutionService = JobExecutionService(jobExecutionRepository, jobRepository, healthIndicator, jobExecutorRegistry, emptySet())

  private fun createJobExecution() =
    JobExecution(JobExecutionId("foo"), JobId("bar"), JobExecution.Status.OK, Instant.now(), null, emptyList(),
      "foobar", Instant.now())

  @Test
  fun `ensure findOne calls JobExecutionRepository findOne`() {
    val jobExecutionId = JobExecutionId("foo")

    every { jobExecutionRepository.findOne(jobExecutionId) } returns null

    jobExecutionService.findOne(jobExecutionId)

    verify { jobExecutionRepository.findOne(jobExecutionId) }
  }

  @Test
  fun `ensure find100DescendingByLastUpdated calls JobExecutionRepository find100DescendingByLastUpdated`() {
    val jobId = JobId("foo")

    every { jobExecutionRepository.find100DescendingByLastUpdated(jobId) } returns emptyList()

    jobExecutionService.find100DescendingByLastUpdated(jobId)

    verify { jobExecutionRepository.find100DescendingByLastUpdated(jobId) }
  }

  @Test
  fun `ensure findAllIgnoreMessages calls JobExecutionRepository findAllIgnoreMessages`() {
    every { jobExecutionRepository.findAllIgnoreMessages() } returns emptyList()

    jobExecutionService.findAllIgnoreMessages()

    verify { jobExecutionRepository.findAllIgnoreMessages() }
  }

  @Test
  fun `ensure save calls JobExecutionRepository save`() {
    val jobExecution = createJobExecution()

    every { jobExecutionRepository.save(jobExecution) } returns createJobExecution()

    jobExecutionService.save(jobExecution)

    verify { jobExecutionRepository.save(jobExecution) }
  }

  @Test
  fun `ensure remove calls JobExecutionRepository remove`() {
    val jobExecution = createJobExecution()

    every { jobExecutionRepository.remove(jobExecution) } returns Unit

    jobExecutionService.remove(jobExecution)

    verify { jobExecutionRepository.remove(jobExecution) }
  }

  @Test
  fun `ensure keepAlive calls JobExecutionRepository keepAlive`() {
    val jobExecutionId = JobExecutionId("foo")

    every { jobExecutionRepository.updateLastUpdated(jobExecutionId, any()) } returns null

    jobExecutionService.keepAlive(jobExecutionId)

    verify { jobExecutionRepository.updateLastUpdated(jobExecutionId, any()) }
  }

  @Test
  fun `ensure appendMessage handles ERROR message correctly`() {
    every {jobExecutionRepository.appendMessage(any(),any()) } returns null
    every {jobExecutionRepository.updateStatus(any(),any()) } returns null
    val jobExecutionId = JobExecutionId("foo")
    val message = JobExecutionMessage(Instant.now(), JobExecutionMessage.Level.ERROR, "foo bar bazz")
    jobExecutionService.appendMessage(jobExecutionId, message)
    verify { jobExecutionRepository.appendMessage(jobExecutionId,message) }
    verify { jobExecutionRepository.updateStatus(jobExecutionId, JobExecution.Status.ERROR) }
  }

  @Test
  fun `ensure appendMessage handles WARNING message correctly`() {
    every {jobExecutionRepository.appendMessage(any(),any()) } returns null
    val jobExecutionId = JobExecutionId("foo")
    val message = JobExecutionMessage(Instant.now(), JobExecutionMessage.Level.WARNING, "foo bar bazz")
    jobExecutionService.appendMessage(jobExecutionId, message)
    verify { jobExecutionRepository.appendMessage(jobExecutionId,message) }
    verify(exactly = 0) { jobExecutionRepository.updateStatus(jobExecutionId, any()) }
  }

  @Test
  fun `ensure appendMessage handles INFO message correctly`() {
    every {jobExecutionRepository.appendMessage(any(),any()) } returns null
    val jobExecutionId = JobExecutionId("foo")
    val message = JobExecutionMessage(Instant.now(), JobExecutionMessage.Level.INFO, "foo bar bazz")
    jobExecutionService.appendMessage(jobExecutionId, message)
    verify { jobExecutionRepository.appendMessage(jobExecutionId,message) }
    verify(exactly = 0) { jobExecutionRepository.updateStatus(jobExecutionId, any()) }
  }
}