package com.breuninger.boot.mongo.configuration;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class MongoPropertiesTest {

  @Test
  public void shouldReturnPassword() {
    final var props = new MongoProperties();
    props.setPassword("somePassword");
    assertThat(props.getPassword(), is("somePassword"));
  }
}
