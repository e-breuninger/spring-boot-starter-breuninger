package com.breuninger.boot.jobs.web

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.repository.JobExecutionRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JobExecutionRestControllerIntegrationTest(
  @Autowired private val restTemplate: TestRestTemplate,
  @Autowired private val jobExecutionRepository: JobExecutionRepository,
  @LocalServerPort private val port: Int
) {

  @BeforeEach
  fun beforeEach() = jobExecutionRepository.drop()

  @Test
  fun `ensure find calls JobExecutionService findOne and returns its result`() {
    val jobExecution = JobExecution(JobExecutionId("foo"), JobId("bar"))
    jobExecutionRepository.save(jobExecution)

    val result = restTemplate.getForEntity<JobExecution>("http://localhost:$port/jobExecutions/foo")

    assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
    assertThat(result.body!!.id).isEqualTo(jobExecution.id)
    assertThat(result.body!!.jobId).isEqualTo(jobExecution.jobId)
  }

  @Test
  fun `ensure create calls JobExecutionService create and returns its result`() {
    val result = restTemplate.postForEntity<Job>("http://localhost:$port/jobExecutions?jobId=bar")

    assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
  }
}
