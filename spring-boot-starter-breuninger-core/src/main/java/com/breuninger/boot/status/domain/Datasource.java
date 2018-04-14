package com.breuninger.boot.status.domain;

import static java.lang.Integer.valueOf;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.util.List;

import com.breuninger.boot.annotations.Beta;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.EqualsAndHashCode;

@Beta
@EqualsAndHashCode
@JsonSerialize(using = ToStringSerializer.class)
public final class Datasource {

  public final String node;
  public final int port;
  public final String resource;

  private Datasource(final String node, final int port, final String resource) {
    this.node = node;
    this.port = port;
    this.resource = resource;
  }

  public static List<Datasource> datasources(final String ds) {
    if (ds.contains(",")) {
      return stream(ds.split(",")).map(Datasource::datasource).collect(toList());
    } else {
      return singletonList(datasource(ds));
    }
  }

  @JsonCreator
  public static Datasource datasource(final String ds) {
    var nodeAndPort = ds;
    var resource = "";

    final var slashPos = ds.indexOf("/");
    if (slashPos != -1) {
      nodeAndPort = ds.substring(0, slashPos);
      resource = ds.substring(slashPos + 1);
    }

    final var colonPos = ds.indexOf(":");
    if (colonPos != -1) {
      return datasource(nodeAndPort.substring(0, colonPos), valueOf(nodeAndPort.substring(colonPos + 1)), resource);
    } else {
      return datasource(nodeAndPort, -1, resource);
    }
  }

  public static Datasource datasource(final String node, final int port, final String resource) {
    return new Datasource(node, port, resource);
  }

  @Override
  public String toString() {
    final var sb = new StringBuilder(node);
    if (port != -1) {
      sb.append(":").append(port);
    }
    if (!resource.isEmpty()) {
      sb.append("/").append(resource);
    }
    return sb.toString();
  }
}
