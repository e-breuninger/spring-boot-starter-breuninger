package com.breuninger.boot.validation.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ErrorHalRepresentationTest {

  @Test
  public void shouldSerializeAndDeserializeWithObjectMapper() throws IOException {
    // given
    final var errorHalRepresentation = ErrorHalRepresentation.builder()
      .withErrorMessage("some error message")
      .withError("field", "key", "message", "rejected")
      .build();
    final var objectMapper = new ObjectMapper();

    // when
    final var json = objectMapper.writeValueAsString(errorHalRepresentation);
    final var deserialized = objectMapper.readValue(json, ErrorHalRepresentation.class);

    // then
    assertThat(deserialized, is(errorHalRepresentation));
  }
}
