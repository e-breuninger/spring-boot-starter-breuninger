package com.breuninger.boot.acceptance.api;

import static java.util.Collections.singletonList;
import static java.util.Optional.of;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.parseMediaType;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import com.breuninger.boot.testsupport.applicationdriver.SpringTestBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FeatureTogglesApi extends SpringTestBase {

  private static final RestTemplate restTemplate = new RestTemplate();
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static String content;
  private static HttpStatus statusCode;

  public static void internal_toggles_is_retrieved_as(final String mediaType) {
    getResource("http://localhost:8085/togglztest/internal/toggles", of(mediaType));
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

  private static void getResource(final String url, final Optional<String> mediaType) {
    final var headers = new HttpHeaders();
    mediaType.ifPresent(s -> headers.setAccept(singletonList(parseMediaType(s))));

    final var responseEntity = restTemplate.exchange(url, GET, new HttpEntity<>("parameters", headers),
      String.class);
    content = responseEntity.getBody();
    statusCode = responseEntity.getStatusCode();
  }
}
