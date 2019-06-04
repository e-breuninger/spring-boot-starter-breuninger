package com.breuninger.boot.jobs.web

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.repository.JobExecutionRepository
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JobExecutionHtmlControllerIntegrationTest(
  // TODO(KA): use WebClient instead of restTemplate
  private val restTemplate: TestRestTemplate,
  private val jobExecutionRepository: JobExecutionRepository,
  @LocalServerPort private val port: Int
) {

  @Test
  fun `ensure that a thymeleaf template is returned without an parsing error for jobExecutions`() {
    val result = restTemplate.getForEntity("http://localhost:$port/jobExecutions", String::class.java)

    assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
    assertThat(result.body!!.contains("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">")).isTrue()
  }

  @Test
  fun `ensure that a thymeleaf template is returned without an parsing error for jobExecutions jobExecutionId`() {
    jobExecutionRepository.drop()
    jobExecutionRepository.save(JobExecution(JobExecutionId("foo"), JobId("bar")))
    val headers = HttpHeaders()
    headers.add(HttpHeaders.ACCEPT, MediaType.TEXT_HTML_VALUE)

    val result = restTemplate.exchange<String>(
      "http://localhost:$port/jobExecutions/foo",
      HttpMethod.GET,
      HttpEntity<Map<String, String>>(headers))

    assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
    assertThat(result.body!!.contains("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">")).isTrue()
  }

  @Test
  fun `ensure that a thymeleaf template is returned without an parsing error for jobExecutions jobId`() {
    jobExecutionRepository.drop()
    jobExecutionRepository.save(JobExecution(JobExecutionId("foo"), JobId("bar")))

    val result = restTemplate.getForEntity("http://localhost:$port/jobExecutions?jobId=bar", String::class.java)

    assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
    assertThat(result.body!!.contains("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">")).isTrue()
  }
}