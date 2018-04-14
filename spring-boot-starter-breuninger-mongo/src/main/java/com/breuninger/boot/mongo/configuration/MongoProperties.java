package com.breuninger.boot.mongo.configuration;

import static java.util.stream.Collectors.toList;

import static com.breuninger.boot.status.domain.Datasource.datasource;
import static com.mongodb.MongoClientOptions.builder;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import com.breuninger.boot.status.domain.Datasource;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@ConfigurationProperties(prefix = "breuninger.mongo")
@Validated
public class MongoProperties {

  private @NotEmpty String[] host = {"localhost"};
  private String authenticationDb = "";
  private @NotEmpty String db;
  private String user = "";
  private String password = "";
  private boolean sslEnabled;
  private @NotEmpty String readPreference = "primaryPreferred";
  private @Min(10) int maxWaitTime = 5000;
  private @Min(10) int connectTimeout = 5000;
  private @Min(10) int defaultReadTimeout = 2000;
  private @Min(10) int defaultWriteTimeout = 2000;
  private @Min(1) int serverSelectionTimeout = 30000;
  private @Valid Status status = new Status();
  private @Valid Connectionpool connectionpool = new Connectionpool();

  public List<Datasource> toDatasources() {
    return Stream.of(getHost()).map(host -> datasource(host + "/" + getDb())).collect(toList());
  }

  public List<ServerAddress> getServers() {
    return Stream.of(host).filter(Objects::nonNull).map(this::toServerAddress).filter(Objects::nonNull).collect(toList());
  }

  public MongoClientOptions toMongoClientOptions(final CodecRegistry codecRegistry) {
    return builder().sslEnabled(sslEnabled)
      .codecRegistry(codecRegistry)
      .readPreference(ReadPreference.valueOf(readPreference))
      .connectTimeout(connectTimeout)
      .serverSelectionTimeout(serverSelectionTimeout)
      .cursorFinalizerEnabled(true)
      .maxWaitTime(maxWaitTime)
      .maxConnectionLifeTime(connectionpool.getMaxLifeTime())
      .threadsAllowedToBlockForConnectionMultiplier(connectionpool.getBlockedConnectionMultiplier())
      .maxConnectionIdleTime(connectionpool.getMaxIdleTime())
      .minConnectionsPerHost(connectionpool.getMinSize())
      .connectionsPerHost(connectionpool.getMaxSize())
      .build();
  }

  private ServerAddress toServerAddress(final String server) {
    try {
      if (server.contains(":")) {
        final var hostNamePortPair = server.split(":");
        return new ServerAddress(hostNamePortPair[0], Integer.parseInt(hostNamePortPair[1]));
      } else {
        return new ServerAddress(server);
      }
    } catch (final NumberFormatException e) {
      LOG.warn("Invalid portNumber: " + e.getMessage(), e);
      return null;
    }
  }

  @Getter
  @Setter
  public static class Status {

    private boolean enabled = true;
  }

  @Getter
  @Setter
  public static class Connectionpool {

    private @Min(1) int maxSize = 100;
    private @Min(0) int minSize = 2;
    private @Min(1) int blockedConnectionMultiplier = 2;
    private @Min(1) int maxLifeTime = 100000;
    private @Min(1) int maxIdleTime = 10000;
  }
}
