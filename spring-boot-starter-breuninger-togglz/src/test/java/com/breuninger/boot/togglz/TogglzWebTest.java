package com.breuninger.boot.togglz;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import com.breuninger.boot.testsupport.applicationdriver.SpringTestBase;

public class TogglzWebTest extends SpringTestBase {

  private static final RestTemplate restTemplate = new RestTemplate();

  @Test
  public void shouldRegisterTogglzConsole() {
    final var response = restTemplate.getForEntity("http://localhost:8085/togglztest/internal/toggles/",
      String.class);
    assertThat(response.getStatusCode().is2xxSuccessful(), is(true));
  }

  @Test
  public void shouldAllowToggleStateToBeRetrievedInRequests() {
    final var response = restTemplate.getForEntity("http://localhost:8085/togglztest/featurestate/test",
      String.class);
    assertThat(response.getStatusCode().is2xxSuccessful(), is(true));
    assertThat(response.getBody(), is("feature is active"));
  }
}
