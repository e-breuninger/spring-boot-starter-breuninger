package com.breuninger.boot.example.service;

import static java.time.format.DateTimeFormatter.ofLocalizedTime;
import static java.time.format.FormatStyle.SHORT;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.stereotype.Service;

import io.micrometer.core.annotation.Timed;

@Service
public class HelloService {

  private static final Random random = new Random(42);

  @Timed("HelloService.getName")
  public String getName() throws InterruptedException {
    final var someTime = 10 * random.nextInt(100);
    Thread.sleep(someTime);
    return "Breuninger " + ofLocalizedTime(SHORT).format(LocalDateTime.now());
  }
}
