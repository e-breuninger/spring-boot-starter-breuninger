package com.breuninger.boot.jobs.web

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.repository.JobExecutionRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class JobExecutionHtmlControllerTest(
  @Autowired private val restTemplate: TestRestTemplate,
  @Autowired private val jobExecutionRepository: JobExecutionRepository,
  @LocalServerPort private val port: Int) {

  @Test
  fun `ensure that a thymeleaf template is returned without an parsing error for jobExecutions`() {
    val result = restTemplate.getForEntity("http://localhost:" + port + "/jobExecutions",
      String::class.java)
    assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
  }

  @Test
  fun `ensure that a thymeleaf template is returned without an parsing error for jobExecutions jobExecutionId`() {
    jobExecutionRepository.clear()
    jobExecutionRepository.save(JobExecution(JobExecutionId("foo"), JobId("bar")))
    val headers = HttpHeaders()
    headers.add("Accept", "text/html")

    val result = restTemplate.exchange<String>("http://localhost:" + port + "/jobExecutions/foo",HttpMethod.GET, HttpEntity<MutableMap<String,String>>(headers))
    assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
  }

  @Test
  fun `ensure that a thymeleaf template is returned without an parsing error for jobExecutions jobId`() {
    jobExecutionRepository.clear()
    jobExecutionRepository.save(JobExecution(JobExecutionId("foo"), JobId("bar")))
    val result = restTemplate.getForEntity("http://localhost:" + port + "/jobExecutions?jobId=bar",
      String::class.java)
    assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
  }

}
