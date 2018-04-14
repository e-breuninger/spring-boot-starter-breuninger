package com.breuninger.boot.health.indicator;

import static org.springframework.boot.actuate.health.Health.up;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public final class ApplicationHealthIndicator implements HealthIndicator {

  private volatile Health lastHealth = up().build();

  public void indicateHealth(final Health health) {
    lastHealth = health;
  }

  @Override
  public Health health() {
    return lastHealth;
  }
}
