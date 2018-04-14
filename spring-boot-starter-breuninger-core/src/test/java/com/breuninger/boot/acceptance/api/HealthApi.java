package com.breuninger.boot.acceptance.api;

import static java.util.Collections.singletonList;

import static org.springframework.boot.actuate.health.Health.down;
import static org.springframework.boot.actuate.health.Health.up;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.parseMediaType;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.breuninger.boot.health.indicator.ApplicationHealthIndicator;
import com.breuninger.boot.testsupport.applicationdriver.SpringTestBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HealthApi extends SpringTestBase {

  private static final RestTemplate restTemplate = new RestTemplate();
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static String content;
  private static HttpStatus statusCode;

  public static void an_healthy_application() {
    final var healthIndicator = applicationContext().getBean(ApplicationHealthIndicator.class);
    healthIndicator.indicateHealth(up().build());
  }

  public static void an_unhealthy_application() {
    final var healthIndicator = applicationContext().getBean(ApplicationHealthIndicator.class);
    healthIndicator.indicateHealth(down().build());
  }

  public static void the_internal_health_is_retrieved() {
    getResource("http://localhost:8084/testcore/actuator/health", Optional.empty());
  }

  private static void getResource(final String url, final Optional<String> mediaType) {
    final var headers = new HttpHeaders();
    mediaType.ifPresent(s -> headers.setAccept(singletonList(parseMediaType(s))));
    try {
      final var responseEntity = restTemplate.exchange(url, GET, new HttpEntity<>("parameters", headers),
        String.class);
      content = responseEntity.getBody();
      statusCode = responseEntity.getStatusCode();
    } catch (final HttpStatusCodeException e) {
      content = e.getStatusText();
      statusCode = e.getStatusCode();
    }
  }

  public static HttpStatus the_status_code() {
    return statusCode;
  }

  public static String the_returned_content() {
    return content;
  }

  public static JsonNode the_returned_json() {
    try {
      return objectMapper.readTree(content);
    } catch (final IOException e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }
}
