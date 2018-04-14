package com.breuninger.boot.status.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static com.breuninger.boot.status.domain.ClusterInfo.clusterInfo;

import org.junit.Test;

public class ClusterInfoTest {

  @Test
  public void shouldGetClusterInfo() {
    final var clusterInfo = clusterInfo(() -> "Foo", () -> "Bar");

    assertThat(clusterInfo.getColor(), is("Foo"));
    assertThat(clusterInfo.getColorState(), is("Bar"));
  }
}
