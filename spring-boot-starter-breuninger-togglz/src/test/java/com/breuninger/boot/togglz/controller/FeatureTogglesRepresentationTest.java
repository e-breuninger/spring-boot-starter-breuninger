package com.breuninger.boot.togglz.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import static com.breuninger.boot.togglz.controller.FeatureTogglesRepresentation.togglzRepresentation;

import org.junit.Test;

import com.breuninger.boot.togglz.EmptyFeatures;
import com.breuninger.boot.togglz.TestFeatures;

public class FeatureTogglesRepresentationTest {

  private FeatureTogglesRepresentation testee;

  @Test
  public void testGetFeatureRepresentation() {
    testee = togglzRepresentation(() -> TestFeatures.class);

    final var features = testee.features;
    assertThat(features.get("TEST_FEATURE"), is(new FeatureToggleRepresentation("a test feature toggle", true, null)));
  }

  @Test
  public void testGetEmptyFeatureRepresentation() {
    testee = togglzRepresentation(() -> EmptyFeatures.class);

    final var features = testee.features;
    assertThat(features, is(notNullValue()));
    assertThat(features.isEmpty(), is(true));
  }
}
