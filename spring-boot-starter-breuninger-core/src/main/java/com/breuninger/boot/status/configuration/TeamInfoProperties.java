package com.breuninger.boot.status.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "breuninger.status.team")
public class TeamInfoProperties {

  private String name;
  private String technicalContact;
  private String businessContact;

  public static TeamInfoProperties teamInfoProperties(final String name, final String technicalContact,
                                                      final String businessContact) {
    final var teamInfoProperties = new TeamInfoProperties();
    teamInfoProperties.name = name;
    teamInfoProperties.technicalContact = technicalContact;
    teamInfoProperties.businessContact = businessContact;
    return teamInfoProperties;
  }
}
