package com.breuninger.boot.status.domain;

import static java.util.Arrays.asList;

import java.util.List;

import com.breuninger.boot.annotations.Beta;

@Beta
public class DatasourceDependencyBuilder {

  private String name;
  private String description;
  private String type;
  private String subtype;
  private List<Datasource> datasources;
  private Criticality criticality;
  private Expectations expectations;

  public static DatasourceDependencyBuilder copyOf(final DatasourceDependency prototype) {
    return new DatasourceDependencyBuilder().withName(prototype.getName())
      .withDescription(prototype.getDescription())
      .withType(prototype.getType())
      .withSubtype(prototype.getSubtype())
      .withDatasources(prototype.getDatasources())
      .withCriticality(prototype.getCriticality())
      .withExpectations(prototype.getExpectations());
  }

  public static DatasourceDependencyBuilder mongoDependency(final List<Datasource> datasources) {
    return new DatasourceDependencyBuilder().withDatasources(datasources)
      .withType(DatasourceDependency.TYPE_DB)
      .withSubtype(DatasourceDependency.SUBTYPE_MONGODB);
  }

  public static DatasourceDependencyBuilder mongoDependency(final Datasource... datasources) {
    return mongoDependency(asList(datasources));
  }

  public static DatasourceDependencyBuilder redisDependency(final List<Datasource> datasources) {
    return new DatasourceDependencyBuilder().withDatasources(datasources)
      .withType(DatasourceDependency.TYPE_DB)
      .withSubtype(DatasourceDependency.SUBTYPE_REDIS);
  }

  public static DatasourceDependencyBuilder redisDependency(final Datasource... datasources) {
    return redisDependency(asList(datasources));
  }

  public static DatasourceDependencyBuilder cassandraDependency(final List<Datasource> datasources) {
    return new DatasourceDependencyBuilder().withDatasources(datasources)
      .withType(DatasourceDependency.TYPE_DB)
      .withSubtype(DatasourceDependency.SUBTYPE_CASSANDRA);
  }

  public static DatasourceDependencyBuilder cassandraDependency(final Datasource... datasources) {
    return cassandraDependency(asList(datasources));
  }

  public static DatasourceDependencyBuilder elasticSearchDependency(final List<Datasource> datasources) {
    return new DatasourceDependencyBuilder().withDatasources(datasources)
      .withType(DatasourceDependency.TYPE_DB)
      .withSubtype(DatasourceDependency.SUBTYPE_ELASTICSEARCH);
  }

  public static DatasourceDependencyBuilder elasticSearchDependency(final Datasource... datasources) {
    return elasticSearchDependency(asList(datasources));
  }

  public static DatasourceDependencyBuilder kafkaDependency(final List<Datasource> datasources) {
    return new DatasourceDependencyBuilder().withDatasources(datasources)
      .withType(DatasourceDependency.TYPE_QUEUE)
      .withSubtype(DatasourceDependency.SUBTYPE_KAFKA);
  }

  public static DatasourceDependencyBuilder kafkaDependency(final Datasource... datasources) {
    return kafkaDependency(asList(datasources));
  }

  public static DatasourceDependencyBuilder datasourceDependency(final List<Datasource> datasources) {
    return new DatasourceDependencyBuilder().withDatasources(datasources);
  }

  public static DatasourceDependencyBuilder datasourceDependency(final Datasource... datasources) {
    return datasourceDependency(asList(datasources));
  }

  public DatasourceDependencyBuilder withName(final String name) {
    this.name = name;
    return this;
  }

  public DatasourceDependencyBuilder withDescription(final String description) {
    this.description = description;
    return this;
  }

  public DatasourceDependencyBuilder withType(final String type) {
    this.type = type;
    return this;
  }

  public DatasourceDependencyBuilder withSubtype(final String subtype) {
    this.subtype = subtype;
    return this;
  }

  private DatasourceDependencyBuilder withDatasources(final List<Datasource> dataSources) {
    datasources = dataSources;
    return this;
  }

  public DatasourceDependencyBuilder withCriticality(final Criticality criticality) {
    this.criticality = criticality;
    return this;
  }

  public DatasourceDependencyBuilder withExpectations(final Expectations expectations) {
    this.expectations = expectations;
    return this;
  }

  public DatasourceDependency build() {
    return new DatasourceDependency(name, description, type, subtype, datasources, criticality, expectations);
  }
}
