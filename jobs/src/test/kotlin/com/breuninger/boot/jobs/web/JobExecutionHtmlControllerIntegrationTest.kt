package com.breuninger.boot.jobs.web

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.repository.JobExecutionRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JobExecutionHtmlControllerIntegrationTest(
  @Autowired private val restTemplate: TestRestTemplate,
  @Autowired private val jobExecutionRepository: JobExecutionRepository,
  @LocalServerPort private val port: Int
) {

  @BeforeEach
  fun beforeEach() {
    jobExecutionRepository.drop()
    jobExecutionRepository.save(JobExecution(JobExecutionId("foo"), JobId("bar")))
  }

  @Test
  fun `ensure that a thymeleaf template is returned without an parsing error for jobExecutions`() {
    val result = restTemplate.getForEntity<String>("http://localhost:$port/jobExecutions")

    assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
    assertThat(result.body!!).contains("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">")
  }

  @Test
  fun `ensure that a thymeleaf template is returned without an parsing error for jobExecutions jobExecutionId`() {
    val headers = HttpHeaders()
    headers.add(HttpHeaders.ACCEPT, MediaType.TEXT_HTML_VALUE)

    val result = restTemplate.exchange<String>(
      "http://localhost:$port/jobExecutions/foo",
      HttpMethod.GET,
      HttpEntity<Map<String, String>>(headers))

    assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
    assertThat(result.body!!).contains("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">")
  }

  @Test
  fun `ensure that a thymeleaf template is returned without an parsing error for jobExecutions jobId`() {
    val result = restTemplate.getForEntity<String>("http://localhost:$port/jobExecutions?jobId=bar")

    assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
    assertThat(result.body!!).contains("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">")
  }
}
