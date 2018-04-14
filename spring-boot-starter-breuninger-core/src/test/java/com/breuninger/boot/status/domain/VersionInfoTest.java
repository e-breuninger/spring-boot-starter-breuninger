package com.breuninger.boot.status.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import static com.breuninger.boot.status.configuration.VersionInfoProperties.versionInfoProperties;
import static com.breuninger.boot.status.domain.VersionInfo.versionInfo;

import java.util.Properties;

import org.junit.Test;
import org.springframework.boot.info.GitProperties;

public class VersionInfoTest {

  @Test
  public void shouldReplaceCommitHashInTemplate() {
    // given
    final var versionInfo = versionInfo(versionInfoProperties("42.0.1-RELEASE", "ab0816", "http://example.org/test/{commit}"));
    // then
    assertThat(versionInfo.url, is("http://example.org/test/ab0816"));
  }

  @Test
  public void shouldReplaceVersionInTemplate() {
    // given
    final var versionInfo = versionInfo(versionInfoProperties("42.0.1-RELEASE", "ab0816", "http://example.org/test/{version}"));
    // then
    assertThat(versionInfo.url, is("http://example.org/test/42.0.1-RELEASE"));
  }

  @Test
  public void shouldBuildVersionInfoFromGitProperties() {
    // given
    final var versionInfo = versionInfo(versionInfoProperties("42.0.1-RELEASE", "ignored", "http://example.org/test/{version}"),
      gitProperties("abc4711"));
    // then
    assertThat(versionInfo.commitId, is("abc4711"));
    assertThat(versionInfo.version, is("42.0.1-RELEASE"));
    assertThat(versionInfo.url, is("http://example.org/test/42.0.1-RELEASE"));
  }

  private GitProperties gitProperties(final String commitId) {
    return new GitProperties(new Properties() {{
      put("commit.id", commitId);
    }});
  }
}
