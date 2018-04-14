package com.breuninger.boot.logging;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "breuninger.logging")
@Validated
public class LoggingProperties {

  private Header header;
  private Ui ui;

  @Getter
  @Setter
  public static class Header {

    private boolean enabled = true;
    private String names = "X-Origin";
  }

  @Getter
  @Setter
  public static class Ui {

    private boolean enabled = true;
  }
}
