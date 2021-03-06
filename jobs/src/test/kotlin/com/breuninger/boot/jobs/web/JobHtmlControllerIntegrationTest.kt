package com.breuninger.boot.jobs.web

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.repository.JobRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JobHtmlControllerIntegrationTest(
  @Autowired private val restTemplate: TestRestTemplate,
  @Autowired private val jobRepository: JobRepository,
  @LocalServerPort private val port: Int
) {

  @BeforeEach
  fun beforeEach() {
    jobRepository.drop()
    jobRepository.create(Job(JobId("foo")))
  }

  @Test
  fun `ensure that a thymeleaf template is returned without an parsing error for jobs`() {
    val result = restTemplate.getForEntity<String>("http://localhost:$port/jobs")

    assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
    assertThat(result.body!!).contains("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">")
  }

  @Test
  fun `ensure that a thymeleaf template is returned without an parsing error for jobs jobId`() {
    val result = restTemplate.getForEntity<String>("http://localhost:$port/jobs/NotTimedJob")

    assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
    assertThat(result.body!!).contains("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">")
  }
}
