package com.breuninger.boot.health.indicator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.boot.actuate.health.Health.down;
import static org.springframework.boot.actuate.health.Health.up;
import static org.springframework.boot.actuate.health.Status.DOWN;
import static org.springframework.boot.actuate.health.Status.UP;

import org.junit.Test;

public class ApplicationHealthIndicatorTest {

  @Test
  public void shouldIndicateHealth() {
    // given
    final var indicator = new ApplicationHealthIndicator();

    // when
    indicator.indicateHealth(down().build());

    // then
    assertThat(indicator.health().getStatus(), is(DOWN));
  }

  @Test
  public void shouldIndicateHealthOkAfterError() {
    // given
    final var indicator = new ApplicationHealthIndicator();
    indicator.indicateHealth(down().build());

    // when
    indicator.indicateHealth(up().build());

    // then
    assertThat(indicator.health().getStatus(), is(UP));
  }
}
