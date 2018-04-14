package com.breuninger.boot.acceptance.togglz;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import com.breuninger.boot.acceptance.api.FeatureTogglesApi;

public class FeatureTogglesControllerAcceptanceTest {

  @Test
  public void shouldTogglesAsJson() {
    FeatureTogglesApi.internal_toggles_is_retrieved_as("application/json");

    assertThat(FeatureTogglesApi.the_returned_json().at("/features/TEST_FEATURE/description").asText(),
      is("a test feature toggle"));
    assertThat(FeatureTogglesApi.the_returned_json().at("/features/TEST_FEATURE/enabled").asBoolean(), is(true));
    assertThat(FeatureTogglesApi.the_returned_json().at("/features/TEST_FEATURE_2/description").asText(), is("TEST_FEATURE_2"));
    assertThat(FeatureTogglesApi.the_returned_json().at("/features/TEST_FEATURE_2/enabled").asBoolean(), is(true));
  }
}
