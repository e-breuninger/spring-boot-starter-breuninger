package com.breuninger.boot.togglz.web

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
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
internal class TogglzHtmlControllerTest(
  @Autowired private val restTemplate: TestRestTemplate,
  @LocalServerPort private val port: Int) {

  @Test
  fun `ensure that a thymeleaf template is returned without an parsing error for togglz`() {
    val headers = HttpHeaders()
    headers.add("Accept", "text/html")

    val result = restTemplate.exchange<String>("http://localhost:$port/togglz", HttpMethod.GET, HttpEntity<MutableMap<String,String>>(headers))
    assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
    assertThat(result.body!!.contains("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">")).isTrue()
  }

}
