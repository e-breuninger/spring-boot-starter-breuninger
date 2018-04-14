package com.breuninger.boot.status.domain;

import com.breuninger.boot.annotations.Beta;
import com.breuninger.boot.status.configuration.TeamInfoProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.jcip.annotations.Immutable;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@Beta
@Immutable
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamInfo {

  public final String name;
  public final String technicalContact;
  public final String businessContact;

  private TeamInfo(final String name, final String technicalContact, final String businessContact) {
    this.name = name;
    this.technicalContact = technicalContact;
    this.businessContact = businessContact;
  }

  public static TeamInfo teamInfo(final TeamInfoProperties teamInfoProperties) {
    return new TeamInfo(teamInfoProperties.getName(), teamInfoProperties.getTechnicalContact(),
      teamInfoProperties.getBusinessContact());
  }
}
