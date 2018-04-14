package com.breuninger.boot.status.domain;

import static com.breuninger.boot.status.domain.Criticality.NOT_SPECIFIED;

import com.breuninger.boot.annotations.Beta;

import net.jcip.annotations.Immutable;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@Beta
@Immutable
@EqualsAndHashCode
@ToString
public class ServiceType {

  public static final String TYPE_REST_SERVICE = "service/rest";
  public static final String TYPE_DATA_IMPORT = "data/import/full";
  public static final String TYPE_DATA_FEED = "data/import/delta";

  public final String type;
  public final Criticality criticality;
  public final String disasterImpact;

  private ServiceType(final String type, final Criticality criticality, final String disasterImpact) {
    this.type = type;
    this.criticality = criticality;
    this.disasterImpact = disasterImpact;
  }

  public static ServiceType serviceType(final String type, final Criticality criticality, final String disasterImpact) {
    return new ServiceType(type, criticality, disasterImpact);
  }

  public static ServiceType unspecifiedService() {
    return new ServiceType("not specified", NOT_SPECIFIED, "not specified");
  }
}
