package com.breuninger.boot.status.domain;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;

import java.util.List;

import com.breuninger.boot.annotations.Beta;

import net.jcip.annotations.Immutable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Beta
@Immutable
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class DatasourceDependency extends ExternalDependency {

  public static final String TYPE_DB = "db";
  public static final String TYPE_QUEUE = "queue";

  public static final String SUBTYPE_CASSANDRA = "Cassandra";
  public static final String SUBTYPE_MONGODB = "MongoDB";
  public static final String SUBTYPE_REDIS = "Redis";
  public static final String SUBTYPE_ELASTICSEARCH = "ElasticSearch";
  public static final String SUBTYPE_KAFKA = "Kafka";

  private final List<Datasource> datasources;

  DatasourceDependency() {
    this(null, null, "", "", emptyList(), null, null);
  }

  public DatasourceDependency(final String name, final String description, final String type, final String subtype,
                              final List<Datasource> datasources, final Criticality criticality,
                              final Expectations expectations) {
    super(name, description, type, subtype, criticality, expectations);
    this.datasources = requireNonNull(datasources, "Parameter 'datasources' must not be null");
  }
}
