package com.breuninger.boot.example.health;

import static java.time.LocalDate.now;

import static org.springframework.boot.actuate.health.Health.down;
import static org.springframework.boot.actuate.health.Health.up;

import java.time.DayOfWeek;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MondayHatingHealthIndicator implements HealthIndicator {

  @Override
  public Health health() {
    if (now().getDayOfWeek() == DayOfWeek.MONDAY) {
      return down().build();
    }
    return up().build();
  }
}
