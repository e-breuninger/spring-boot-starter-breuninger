package com.breuninger.boot.jobs.service

import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.repository.JobRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class JobServiceTest {

  private val jobRepository = mockk<JobRepository>()
  private val jobService = JobService(jobRepository)

  @Test
  fun `ensure findOne calls JobRepository findOne`() {
    val jobId = JobId("foo")
    every { jobRepository.findOne(jobId) } returns null

    jobService.findOne(jobId)

    verify { jobRepository.findOne(jobId) }
  }

  @Test
  fun `ensure findAll calls JobRepository findAll`() {
    every { jobRepository.findAll() } returns emptyList()

    jobService.findAll()

    verify { jobRepository.findAll() }
  }

  @Test
  fun `ensure create calls JobRepository create`() {
    val job = Job(JobId("foo"))
    every { jobRepository.create(job) } returns Unit

    jobService.create(job)

    verify { jobRepository.create(job) }
  }

  @Test
  fun `ensure updateDisableState calls JobRepository updateDisableState`() {
    val jobId = JobId("foo")
    val job = Job(jobId)
    every { jobRepository.updateDisableState(jobId, job) } returns null

    jobService.updateDisableState(jobId, job)

    verify { jobRepository.updateDisableState(jobId, job) }
  }

  @Test
  fun `ensure findState calls JobRepository findState`() {
    val jobId = JobId("foo")
    every { jobRepository.findState(jobId, "foo") } returns null

    jobService.findState(jobId, "foo")

    verify { jobRepository.findState(jobId, "foo") }
  }

  @Test
  fun `ensure updateState calls JobRepository updateState`() {
    val jobId = JobId("foo")
    every { jobRepository.updateState(jobId, "foo", "bar") } returns null

    jobService.updateState(jobId, "foo", "bar")

    verify { jobRepository.updateState(jobId, "foo", "bar") }
  }
}
