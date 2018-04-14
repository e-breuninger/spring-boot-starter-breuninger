package com.breuninger.boot.status.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "breuninger.status.vcs")
public class VersionInfoProperties {

  private String version = "unknown";
  private String commit = "unknown";
  private String urlTemplate = "";
  private String commitTime = "";
  private String userName = "";
  private String userEmail = "";
  private String messageShort = "";
  private String messageFull = "";
  private String branch = "";

  public static VersionInfoProperties versionInfoProperties(final String version, final String commit, final String urlTemplate) {
    final var p = new VersionInfoProperties();
    p.version = version;
    p.commit = commit;
    p.urlTemplate = urlTemplate;
    return p;
  }

  public String getCommitId() {
    return commit;
  }

  public String getCommitIdAbbrev() {
    final var id = getCommitId();
    return id.length() > 7 ? id.substring(0, 7) : id;
  }
}
