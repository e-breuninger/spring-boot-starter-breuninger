package com.breuninger.boot.jobs.web

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.repository.JobRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity.put
import java.net.URI

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JobRestControllerIntegrationTest(
  @Autowired private val restTemplate: TestRestTemplate,
  @Autowired private val jobRepository: JobRepository,
  @LocalServerPort private val port: Int
) {

  @BeforeEach
  fun beforeEach() = jobRepository.drop()

  @Test
  fun `ensure update calls JobService updateDisableState and returns its result`() {
    val job = Job(JobId("foo"))
    jobRepository.create(job)
    val jobToPut = job.copy(disabled = true, disableComment = "disableComment")

    val result = restTemplate.exchange<Job>(put(URI.create("http://localhost:$port/jobs/foo")).body(jobToPut))

    assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
    assertThat(result.body!!).isEqualTo(jobToPut)
  }
}
