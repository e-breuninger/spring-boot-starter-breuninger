package com.breuninger.boot.status.controller;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

import static com.breuninger.boot.configuration.BreuningerApplicationProperties.breuningerApplicationProperties;
import static com.breuninger.boot.status.configuration.VersionInfoProperties.versionInfoProperties;
import static com.breuninger.boot.status.controller.StatusRepresentation.statusRepresentationOf;
import static com.breuninger.boot.status.domain.ApplicationInfo.applicationInfo;
import static com.breuninger.boot.status.domain.ApplicationStatus.applicationStatus;
import static com.breuninger.boot.status.domain.ClusterInfo.clusterInfo;
import static com.breuninger.boot.status.domain.Link.link;
import static com.breuninger.boot.status.domain.Status.OK;
import static com.breuninger.boot.status.domain.Status.WARNING;
import static com.breuninger.boot.status.domain.StatusDetail.statusDetail;
import static com.breuninger.boot.testsupport.util.JsonMap.jsonMapFrom;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.breuninger.boot.status.domain.ApplicationInfo;
import com.breuninger.boot.status.domain.ClusterInfo;
import com.breuninger.boot.status.domain.SystemInfo;
import com.breuninger.boot.status.domain.TeamInfo;
import com.breuninger.boot.status.domain.VersionInfo;
import com.breuninger.boot.testsupport.util.JsonMap;

public class StatusRepresentationTest {

  @Test
  public void shouldCreateStatusRepresentationWithoutDetails() {
    // given
    final var breuningerApplicationProperties = breuningerApplicationProperties("Some Title", "group", "local-env", "desc");
    final var json = statusRepresentationOf(
      applicationStatus(applicationInfo("app-name", breuningerApplicationProperties), mock(ClusterInfo.class),
        mock(SystemInfo.class), mock(VersionInfo.class), mock(TeamInfo.class), emptyList()));
    // then
    assertThat(json.application.name, is("app-name"));
    assertThat(json.application.title, is("Some Title"));
    assertThat(json.application.status, is(OK));
    assertThat(json.application.statusDetails.size(), is(0));
  }

  @Test
  public void shouldCreateStatusRepresentationWithVersionInfo() {
    // given
    final var json = statusRepresentationOf(
      applicationStatus(mock(ApplicationInfo.class), mock(ClusterInfo.class), mock(SystemInfo.class),
        VersionInfo.versionInfo(versionInfoProperties("1.0.0", "0815", "http://example.org/commits/{commit}")),
        mock(TeamInfo.class), emptyList()));
    // then
    assertThat(json.application.version, is("1.0.0"));
    assertThat(json.application.commit, is("0815"));
    assertThat(json.application.vcsUrl, is("http://example.org/commits/0815"));
  }

  @Test
  public void shouldCreateStatusRepresentationWithClusterInfo() {
    // given
    final var cluster = clusterInfo("BLU", "active");
    final var json = statusRepresentationOf(
      applicationStatus(mock(ApplicationInfo.class), cluster, mock(SystemInfo.class), mock(VersionInfo.class),
        mock(TeamInfo.class), emptyList()));
    // then
    assertThat(json.cluster.getColor(), is("BLU"));
    assertThat(json.cluster.getColorState(), is("active"));
  }

  @Test
  public void shouldCreateStatusRepresentationWithoutClusterInfo() {
    // given
    final var cluster = clusterInfo("", "");
    final var json = statusRepresentationOf(
      applicationStatus(mock(ApplicationInfo.class), cluster, mock(SystemInfo.class), mock(VersionInfo.class),
        mock(TeamInfo.class), emptyList()));
    // then
    assertThat(json.cluster, is(nullValue()));
  }

  @Test
  public void shouldCreateStatusRepresentationWithSingleDetail() {
    // given
    final var json = statusRepresentationOf(
      applicationStatus(mock(ApplicationInfo.class), mock(ClusterInfo.class), mock(SystemInfo.class), mock(VersionInfo.class),
        mock(TeamInfo.class), singletonList(statusDetail("someDetail", WARNING, "detailed warning"))));
    // then
    assertThat(json.application.status, is(WARNING));
    final Map<String, String> someDetail = (Map) json.application.statusDetails.get("someDetail");
    assertThat(someDetail.get("status"), is("WARNING"));
    assertThat(someDetail.get("message"), is("detailed warning"));
    assertThat(someDetail.get("link"), is(nullValue()));
  }

  @Test
  public void shouldCreateStatusRepresentationWithDetailInclUrl() {
    // given
    final var json = statusRepresentationOf(
      applicationStatus(mock(ApplicationInfo.class), mock(ClusterInfo.class), mock(SystemInfo.class), mock(VersionInfo.class),
        mock(TeamInfo.class), singletonList(
          statusDetail("someDetail", OK, "some message", link("item", "http://example.org/some/url", "some title")))));
    // then
    assertThat(json.application.status, is(OK));
    final var jsonMap = jsonMapFrom(json.application.statusDetails.get("someDetail"));
    final var link = jsonMap.get("links").asListOf(JsonMap.class).get(0);
    assertThat(link.getString("href"), is("http://example.org/some/url"));
    assertThat(link.getString("title"), is("some title"));
    assertThat(link.getString("rel"), is("item"));
  }

  @Test
  public void shouldCreateStatusRepresentationWithMultipleDetails() {
    // given
    final Map<String, String> detailMap = new HashMap<>();
    detailMap.put("Count", "1000");
    final var json = statusRepresentationOf(
      applicationStatus(mock(ApplicationInfo.class), mock(ClusterInfo.class), mock(SystemInfo.class), mock(VersionInfo.class),
        mock(TeamInfo.class), asList(statusDetail("Some Detail", OK, "perfect"),
          statusDetail("Some Other Detail", WARNING, "detailed warning", detailMap))));
    // then
    assertThat(json.application.status, is(WARNING));
    final Map<String, String> someDetail = (Map) json.application.statusDetails.get("someDetail");
    assertThat(someDetail.get("status"), is("OK"));
    assertThat(someDetail.get("message"), is("perfect"));
    assertThat(someDetail.get("status"), is("OK"));
    final Map<String, String> someOtherDetail = (Map) json.application.statusDetails.get("someOtherDetail");
    assertThat(someOtherDetail.get("status"), is("WARNING"));
    assertThat(someOtherDetail.get("message"), is("detailed warning"));
    assertThat(someOtherDetail.get("count"), is("1000"));
  }
}
