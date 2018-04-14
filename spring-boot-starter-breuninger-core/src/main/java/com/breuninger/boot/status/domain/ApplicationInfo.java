package com.breuninger.boot.status.domain;

import com.breuninger.boot.configuration.BreuningerApplicationProperties;

import net.jcip.annotations.Immutable;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@Immutable
@EqualsAndHashCode
@ToString
public class ApplicationInfo {

  public final String name;
  public final String title;
  public final String description;
  public final String group;
  public final String environment;

  private ApplicationInfo(final String name, final BreuningerApplicationProperties applicationInfoProperties) {
    if (name.isEmpty()) {
      throw new IllegalArgumentException("name must not be empty");
    }
    this.name = name;
    title = applicationInfoProperties.getTitle();
    description = applicationInfoProperties.getDescription();
    group = applicationInfoProperties.getGroup();
    environment = applicationInfoProperties.getEnvironment();
  }

  public static ApplicationInfo applicationInfo(final String serviceName, final BreuningerApplicationProperties statusProps) {
    return new ApplicationInfo(serviceName, statusProps);
  }
}
