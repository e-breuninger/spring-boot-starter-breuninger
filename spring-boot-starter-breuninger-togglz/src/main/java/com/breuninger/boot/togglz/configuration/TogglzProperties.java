package com.breuninger.boot.togglz.configuration;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "breuninger.togglz")
@Validated
public class TogglzProperties {

  private @Min(0) int cacheTtl = 5000;
  private @Valid Console console = new Console();

  @Getter
  @Setter
  public static class Console {

    private boolean enabled = true;
  }
}
