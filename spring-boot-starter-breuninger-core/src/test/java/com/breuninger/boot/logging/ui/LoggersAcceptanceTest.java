package com.breuninger.boot.logging.ui;

import static java.util.Collections.singletonList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_HTML;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.breuninger.boot.navigation.NavBar;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class LoggersAcceptanceTest {

  @Autowired
  private ConfigurableApplicationContext ctx;
  @Autowired
  private TestRestTemplate template;

  private HttpHeaders htmlHeaders;
  private HttpHeaders jsonHeaders;

  @Before
  public void setup() {
    htmlHeaders = new HttpHeaders();
    htmlHeaders.setAccept(singletonList(TEXT_HTML));
    jsonHeaders = new HttpHeaders();
    jsonHeaders.setAccept(singletonList(APPLICATION_JSON));
  }

  @Test
  public void shouldHaveLoggersAsHtml() {
    final var response = template.getForEntity("/internal/loggers?format=html", String.class);
    assertThat(response.getStatusCodeValue(), is(200));
    assertThat(response.getHeaders().getContentType().isCompatibleWith(TEXT_HTML), is(true));
  }

  @Test
  public void shouldHaveLoggersAsHtmlThroughHeader() {
    final HttpEntity<String> httpEntity = new HttpEntity<>("", htmlHeaders);
    final var response = template.exchange("/internal/loggers", GET, httpEntity, String.class);
    assertThat(response.getStatusCodeValue(), is(200));
    assertThat(response.getHeaders().getContentType().isCompatibleWith(TEXT_HTML), is(true));
  }

  @Test
  public void shouldHaveLoggersAsJson() {
    final var response = template.getForEntity("/internal/loggers?format=json", String.class);
    assertThat(response.getStatusCodeValue(), is(200));
    assertThat(response.getHeaders().getContentType().isCompatibleWith(APPLICATION_JSON), is(true));
  }

  @Test
  public void shouldHaveLoggersAsJsonThroughHeader() {
    final HttpEntity<String> httpEntity = new HttpEntity<>("", jsonHeaders);
    final var response = template.exchange("/internal/loggers", GET, httpEntity, String.class);
    assertThat(response.getStatusCodeValue(), is(200));
    assertThat(response.getHeaders().getContentType().isCompatibleWith(APPLICATION_JSON), is(true));
  }

  @Test
  public void shouldHaveLoggersAsJsonAsDefault() {
    final var response = template.getForEntity("/internal/loggers", String.class);
    assertThat(response.getStatusCodeValue(), is(200));
    assertThat(response.getHeaders().getContentType().isCompatibleWith(APPLICATION_JSON), is(true));
  }

  @Test
  public void shouldGetLoggerAsJson() {
    final var response = template.getForEntity("/internal/loggers/ROOT", String.class);
    assertThat(response.getStatusCodeValue(), is(200));
    assertThat(response.getHeaders().getContentType().isCompatibleWith(APPLICATION_JSON), is(true));
    assertThat(response.getBody(), is("{\"configuredLevel\":\"INFO\",\"effectiveLevel\":\"INFO\"}"));
  }

  @Test
  public void shouldPostLoggerAsJson() {
    final var headers = new HttpHeaders();
    headers.setAccept(singletonList(APPLICATION_JSON));
    headers.setContentType(APPLICATION_JSON);
    final var postResponse = template.exchange("/internal/loggers/ROOT", POST,
      new HttpEntity<>("{\"configuredLevel\":\"WARN\"}", headers), String.class);
    assertThat(postResponse.getStatusCodeValue(), is(200));

    final var response = template.exchange("/internal/loggers/ROOT", GET, new HttpEntity<>("", headers),
      String.class);
    assertThat(response.getStatusCodeValue(), is(200));
    assertThat(response.getHeaders().getContentType().isCompatibleWith(APPLICATION_JSON), is(true));
    assertThat(response.getBody(), is("{\"configuredLevel\":\"WARN\",\"effectiveLevel\":\"WARN\"}"));
  }

  @Test
  public void shouldHaveLoggersInRightNavBar() {
    final var rightNavBar = ctx.getBean("rightNavBar", NavBar.class);
    assertThat(rightNavBar.getItems().stream().anyMatch(item -> item.getTitle().equals("Loggers")), is(true));
  }
}
