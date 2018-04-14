package com.breuninger.boot.jobs.domain;

import lombok.Getter;

@Getter
public enum Level {
  INFO("info"), WARNING("warning"), ERROR("error");

  private final String key;

  Level(final String key) {
    this.key = key;
  }

  public static Level ofKey(final String s) {
    for (final var l : Level.values()) {
      if (l.getKey().equalsIgnoreCase(s)) {
        return l;
      }
    }
    throw new IllegalArgumentException("no level with this key found");
  }
}
