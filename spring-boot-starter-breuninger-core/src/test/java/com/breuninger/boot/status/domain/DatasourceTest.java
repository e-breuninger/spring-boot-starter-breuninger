package com.breuninger.boot.status.domain;

import static org.assertj.core.api.Assertions.assertThat;

import static com.breuninger.boot.status.domain.Datasource.datasource;
import static com.breuninger.boot.status.domain.Datasource.datasources;

import org.junit.Test;

public class DatasourceTest {

  @Test
  public void shouldBuildDatasource() {
    final var testee = datasource("foo", 42, "bar");
    assertThat(testee.node).isEqualTo("foo");
    assertThat(testee.port).isEqualTo(42);
    assertThat(testee.resource).isEqualTo("bar");
  }

  @Test
  public void shouldSerializeDatasource() {
    final var testee = datasource("foo", 42, "bar");
    assertThat(testee.toString()).isEqualTo("foo:42/bar");
  }

  @Test
  public void shouldBuildDatasourceFromString() {
    final var testee = datasource("foo:42/bar");
    assertThat(testee.node).isEqualTo("foo");
    assertThat(testee.port).isEqualTo(42);
    assertThat(testee.resource).isEqualTo("bar");
  }

  @Test
  public void shouldBuildDatasourceFromString2() {
    final var testee = datasource("foo:42/bar:foobar/0815");
    assertThat(testee.node).isEqualTo("foo");
    assertThat(testee.port).isEqualTo(42);
    assertThat(testee.resource).isEqualTo("bar:foobar/0815");
  }

  @Test
  public void shouldBuildDatasourceFromStringWithoutPort() {
    final var testee = datasource("foo/bar");
    assertThat(testee.node).isEqualTo("foo");
    assertThat(testee.port).isEqualTo(-1);
    assertThat(testee.resource).isEqualTo("bar");
  }

  @Test
  public void shouldBuildDatasourceFromStringWithoutPortAndResource() {
    final var testee = datasource("foo");
    assertThat(testee.node).isEqualTo("foo");
    assertThat(testee.port).isEqualTo(-1);
    assertThat(testee.resource).isEqualTo("");
  }

  @Test
  public void shouldParseListOfDatasources() {
    final var testee = datasources("foo:42/bar,foobar");
    assertThat(testee.get(0).node).isEqualTo("foo");
    assertThat(testee.get(0).port).isEqualTo(42);
    assertThat(testee.get(0).resource).isEqualTo("bar");
    assertThat(testee.get(1).node).isEqualTo("foobar");
    assertThat(testee.get(1).port).isEqualTo(-1);
    assertThat(testee.get(1).resource).isEqualTo("");
  }
}
