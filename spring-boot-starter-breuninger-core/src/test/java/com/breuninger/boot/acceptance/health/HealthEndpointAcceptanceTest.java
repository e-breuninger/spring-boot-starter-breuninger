package com.breuninger.boot.acceptance.health;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

import org.junit.Ignore;
import org.junit.Test;

import com.breuninger.boot.acceptance.api.HealthApi;

public class HealthEndpointAcceptanceTest {

  @Test
  @Ignore("Disabled, because Spring Boot is caching health checks for one second.")
  public void shouldGetApplicationHealth() {
    HealthApi.an_healthy_application();
    HealthApi.the_internal_health_is_retrieved();

    assertThat(HealthApi.the_status_code(), is(OK));
  }

  @Test
  public void shouldBeUnhealty() {
    HealthApi.an_unhealthy_application();

    HealthApi.the_internal_health_is_retrieved();

    assertThat(HealthApi.the_status_code(), is(SERVICE_UNAVAILABLE));
  }
}
