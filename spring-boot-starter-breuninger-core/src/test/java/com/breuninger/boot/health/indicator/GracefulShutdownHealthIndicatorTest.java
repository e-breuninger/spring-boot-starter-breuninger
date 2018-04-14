package com.breuninger.boot.health.indicator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.springframework.boot.actuate.health.Health.down;
import static org.springframework.boot.actuate.health.Health.up;

import org.junit.Test;

public class GracefulShutdownHealthIndicatorTest {

  @Test
  public void shouldHealthyOnStartup() {
    // given
    final GracefulShutdownHealthIndicator gracefulShutdownHealthIndicator;

    // when
    gracefulShutdownHealthIndicator = new GracefulShutdownHealthIndicator(mock(GracefulShutdownProperties.class));

    // then
    assertThat(gracefulShutdownHealthIndicator.health(), is(up().build()));
  }

  @Test
  public void shouldIndicateErrorWhileShutdown() {
    // given
    class TestGracefulShutdownHealthIndicator extends GracefulShutdownHealthIndicator {
      boolean waitForShutdownCalled;

      TestGracefulShutdownHealthIndicator(final GracefulShutdownProperties properties) {
        super(properties);
      }

      @Override
      void waitForShutdown() throws InterruptedException {
        assertThat(health(), is(down().build()));
        waitForShutdownCalled = true;
        super.waitForShutdown();
      }
    }
    final var gracefulShutdownHealthIndicator = new TestGracefulShutdownHealthIndicator(
      mock(GracefulShutdownProperties.class));
    final var runnable = mock(Runnable.class);

    // when
    gracefulShutdownHealthIndicator.stop(runnable);

    // then
    assertThat(gracefulShutdownHealthIndicator.waitForShutdownCalled, is(true));
  }
}
