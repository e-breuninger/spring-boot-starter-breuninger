package com.breuninger.boot.acceptance.status;

import static java.util.Collections.singletonList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

import static com.breuninger.boot.acceptance.api.StatusApi.internal_is_retrieved_as;
import static com.breuninger.boot.acceptance.api.StatusApi.internal_status_is_retrieved_as;
import static com.breuninger.boot.acceptance.api.StatusApi.the_response_headers;
import static com.breuninger.boot.acceptance.api.StatusApi.the_returned_content;
import static com.breuninger.boot.acceptance.api.StatusApi.the_returned_json;
import static com.breuninger.boot.acceptance.api.StatusApi.the_status_code;

import org.junit.Test;
import org.springframework.http.HttpHeaders;

public class StatusControllerAcceptanceTest {

  @Test
  public void shouldGetInternalStatusAsHtml() {

    internal_status_is_retrieved_as("text/html");

    assertThat(the_status_code().value(), is(200));
    assertThat(the_returned_content(), startsWith("<!DOCTYPE html>"));
    assertThat(the_returned_content(), containsString("<title>Some Test</title>"));
  }

  @Test
  public void shouldGetInternalStatusAsMonitoringStatusJson() {
    internal_status_is_retrieved_as("application/vnd.breuninger.monitoring.status+json");

    assertThat(the_status_code().value(), is(200));
    assertThat(the_response_headers().get("Content-Type"),
      contains("application/vnd.breuninger.monitoring.status+json;charset=UTF-8"));
  }

  @Test
  public void shouldRedirectInternalToStatus() {
    internal_is_retrieved_as("text/html");

    assertThat(the_status_code().value(), is(200));
    assertThat(the_returned_content(), startsWith("<!DOCTYPE html>"));
  }

  @Test
  public void shouldGetApplicationInfo() {
    internal_status_is_retrieved_as("application/json");

    assertThat(the_returned_json().at("/application/name").asText(), is("test-app"));
    assertThat(the_returned_json().at("/application/description").asText(), is("desc"));
    assertThat(the_returned_json().at("/application/environment").asText(), is("test-env"));
    assertThat(the_returned_json().at("/application/group").asText(), is("test-group"));
  }

  @Test
  public void shouldGetVersionInformation() {
    internal_status_is_retrieved_as("application/json");

    assertThat(the_returned_json().at("/application/version").asText(), is("1.0.0"));
    assertThat(the_returned_json().at("/application/commit").asText(), is("ab1234"));
    assertThat(the_returned_json().at("/application/vcsUrl").asText(), is("http://example.org/vcs/1.0.0"));
  }

  @Test
  public void shouldGetTeamInformation() {
    internal_status_is_retrieved_as("application/json");

    assertThat(the_returned_json().at("/team/name").asText(), is("Test Team"));
    assertThat(the_returned_json().at("/team/technicalContact").asText(), is("technical@example.org"));
    assertThat(the_returned_json().at("/team/businessContact").asText(), is("business@example.org"));
  }

  @Test
  public void shouldGetClusterInformation() {
    final var headers = new HttpHeaders();
    headers.put("X-Color", singletonList("BLU"));
    headers.put("X-Staging", singletonList("STAGED"));
    internal_status_is_retrieved_as("application/json", headers);

    assertThat(the_returned_json().at("/cluster/color").asText(), is("BLU"));
    assertThat(the_returned_json().at("/cluster/colorState").asText(), is("STAGED"));
  }

  @Test
  public void shouldGetStatusWithDetails() {
    internal_status_is_retrieved_as("application/json");

    assertThat(the_status_code().value(), is(200));
    assertThat(the_returned_json().at("/application/status").asText(), is("WARNING"));
    assertThat(the_returned_json().at("/application/statusDetails/foo/status").asText(), is("OK"));
    assertThat(the_returned_json().at("/application/statusDetails/bar/status").asText(), is("WARNING"));
  }

  @Test
  public void shouldGetStatusWithCriticality() {
    internal_status_is_retrieved_as("application/json");

    assertThat(the_status_code().value(), is(200));
    assertThat(the_returned_json().at("/criticality/level").asText(), is("LOW"));
    assertThat(the_returned_json().at("/criticality/disasterImpact").asText(), is("some impact"));
  }

  @Test
  public void shouldGetStatusWithDependencies() {
    internal_status_is_retrieved_as("application/json");

    assertThat(the_status_code().value(), is(200));
    assertThat(the_returned_json().at("/dependencies/0/url").asText(), is("http://example.com/foo"));
  }
}
