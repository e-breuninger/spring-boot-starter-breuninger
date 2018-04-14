package com.breuninger.boot.status.domain;

import static java.util.Collections.singletonList;

import static org.assertj.core.api.Assertions.assertThat;

import static com.breuninger.boot.status.domain.Datasource.datasource;
import static com.breuninger.boot.status.domain.DatasourceDependencyBuilder.cassandraDependency;
import static com.breuninger.boot.status.domain.DatasourceDependencyBuilder.copyOf;
import static com.breuninger.boot.status.domain.DatasourceDependencyBuilder.datasourceDependency;
import static com.breuninger.boot.status.domain.DatasourceDependencyBuilder.elasticSearchDependency;
import static com.breuninger.boot.status.domain.DatasourceDependencyBuilder.kafkaDependency;
import static com.breuninger.boot.status.domain.DatasourceDependencyBuilder.mongoDependency;
import static com.breuninger.boot.status.domain.DatasourceDependencyBuilder.redisDependency;

import org.junit.Test;

public class DatasourceDependencyBuilderTest {

  @Test
  public void shouldBuildDatasourceDependency() {
    final var dependency = datasourceDependency(datasource("foo:42/bar")).withType("test")
      .withSubtype("unittest")
      .withName("name")
      .withDescription("description")
      .build();
    assertThat(dependency.getName()).isEqualTo("name");
    assertThat(dependency.getDescription()).isEqualTo("description");
    assertThat(dependency.getType()).isEqualTo("test");
    assertThat(dependency.getSubtype()).isEqualTo("unittest");
    assertThat(dependency.getDatasources()).contains(datasource("foo", 42, "bar"));
  }

  @Test
  public void shouldCopyDatasource() {
    final var dependency = mongoDependency(singletonList(datasource("foo"))).build();
    assertThat(dependency).isEqualTo(copyOf(dependency).build());
    assertThat(dependency.hashCode()).isEqualTo(copyOf(dependency).build().hashCode());
  }

  @Test
  public void shouldBuildMongoDatasource() {
    final var dependency = mongoDependency(singletonList(datasource("foo"))).build();
    assertThat(dependency.getDatasources()).contains(datasource("foo", -1, ""));
    assertThat(dependency.getType()).isEqualTo("db");
    assertThat(dependency.getSubtype()).isEqualTo("MongoDB");
  }

  @Test
  public void shouldBuildCassandraDatasource() {
    final var dependency = cassandraDependency(datasource("foo")).build();
    assertThat(dependency.getDatasources()).contains(datasource("foo", -1, ""));
    assertThat(dependency.getType()).isEqualTo("db");
    assertThat(dependency.getSubtype()).isEqualTo("Cassandra");
  }

  @Test
  public void shouldBuildRedisDatasource() {
    final var dependency = redisDependency(datasource("foo")).build();
    assertThat(dependency.getDatasources()).contains(datasource("foo", -1, ""));
    assertThat(dependency.getType()).isEqualTo("db");
    assertThat(dependency.getSubtype()).isEqualTo("Redis");
  }

  @Test
  public void shouldBuildElasticSearchDatasource() {
    final var dependency = elasticSearchDependency(datasource("foo")).build();
    assertThat(dependency.getDatasources()).contains(datasource("foo", -1, ""));
    assertThat(dependency.getType()).isEqualTo("db");
    assertThat(dependency.getSubtype()).isEqualTo("ElasticSearch");
  }

  @Test
  public void shouldBuildKafkaDatasource() {
    final var dependency = kafkaDependency(datasource("foo")).build();
    assertThat(dependency.getDatasources()).contains(datasource("foo", -1, ""));
    assertThat(dependency.getType()).isEqualTo("queue");
    assertThat(dependency.getSubtype()).isEqualTo("Kafka");
  }
}
