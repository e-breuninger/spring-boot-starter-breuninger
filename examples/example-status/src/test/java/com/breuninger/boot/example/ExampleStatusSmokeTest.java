package com.breuninger.boot.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleStatusServer.class, webEnvironment = RANDOM_PORT)
public class ExampleStatusSmokeTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  public void shouldRenderMainPage() {
    final var response = restTemplate.getForEntity("/", String.class);
    assertThat(response.getStatusCodeValue()).isEqualTo(200);
    assertThat(response.getBody()).startsWith("<html");
  }

  @Test
  public void shouldHaveStatusEndpoint() {
    final var response = restTemplate.getForEntity("/internal/status", String.class);
    assertThat(response.getStatusCodeValue()).isEqualTo(200);
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON_UTF8);
    assertThat(response.getBody()).startsWith("{");
  }

  @Test
  public void shouldHaveHealthCheck() {
    final var response = restTemplate.getForEntity("/actuator/health", String.class);
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON_UTF8);
    assertThat(response.getStatusCodeValue()).isIn(200, 503);
  }
}
