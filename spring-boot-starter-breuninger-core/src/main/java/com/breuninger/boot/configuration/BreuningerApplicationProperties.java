package com.breuninger.boot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "breuninger.application")
@Getter
@Setter
public class BreuningerApplicationProperties {

  private String title = "Breuninger µService";
  private String group = "";
  private String environment = "unknown";
  private String description = "";
  private Management management = new Management("/internal");

  public static BreuningerApplicationProperties breuningerApplicationProperties(final String title, final String group,
                                                                                final String environment,
                                                                                final String description) {
    final var breuningerApplicationProperties = new BreuningerApplicationProperties();
    breuningerApplicationProperties.setTitle(title);
    breuningerApplicationProperties.setGroup(group);
    breuningerApplicationProperties.setEnvironment(environment);
    breuningerApplicationProperties.setDescription(description);
    breuningerApplicationProperties.setManagement(new Management("/internal"));
    return breuningerApplicationProperties;
  }

  public static BreuningerApplicationProperties breuningerApplicationProperties(final String title, final String group,
                                                                                final String environment,
                                                                                final String description,
                                                                                final Management management) {
    final var breuningerApplicationProperties = new BreuningerApplicationProperties();
    breuningerApplicationProperties.setTitle(title);
    breuningerApplicationProperties.setGroup(group);
    breuningerApplicationProperties.setEnvironment(environment);
    breuningerApplicationProperties.setDescription(description);
    breuningerApplicationProperties.setManagement(management);
    return breuningerApplicationProperties;
  }

  @AllArgsConstructor
  @Getter
  @Setter
  public static class Management {

    private String basePath;
  }
}
