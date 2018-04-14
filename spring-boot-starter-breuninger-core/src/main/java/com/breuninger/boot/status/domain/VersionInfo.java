package com.breuninger.boot.status.domain;

import java.util.Objects;

import org.springframework.boot.info.GitProperties;

import com.breuninger.boot.status.configuration.VersionInfoProperties;

import net.jcip.annotations.Immutable;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@Immutable
@EqualsAndHashCode
@ToString
public class VersionInfo {

  private static final String COMMIT_TIME = "commit.time";
  private static final String USER_NAME = "commit.user.name";
  private static final String USER_EMAIL = "commit.user.email";
  private static final String MESSAGE_SHORT = "commit.message.short";
  private static final String MESSAGE_FULL = "commit.message.full";

  public final String version;
  public final String commitId;
  public final String commitIdAbbrev;
  public final String commitTime;
  public final String userName;
  public final String userEmail;
  public final String messageShort;
  public final String messageFull;
  public final String branch;
  public final String url;

  private VersionInfo(final VersionInfoProperties versionInfoProperties, final GitProperties gitProperties) {
    if (gitProperties != null) {
      commitId = gitProperties.getCommitId();
      commitIdAbbrev = gitProperties.getShortCommitId();
      branch = gitProperties.getBranch();
      commitTime = gitProperties.get(COMMIT_TIME);
      userName = gitProperties.get(USER_NAME);
      userEmail = gitProperties.get(USER_EMAIL);
      messageShort = gitProperties.get(MESSAGE_SHORT);
      messageFull = gitProperties.get(MESSAGE_FULL);
    } else {
      commitId = versionInfoProperties.getCommitId();
      commitIdAbbrev = versionInfoProperties.getCommitIdAbbrev();
      commitTime = versionInfoProperties.getCommitTime();
      userName = versionInfoProperties.getUserName();
      userEmail = versionInfoProperties.getUserEmail();
      messageShort = versionInfoProperties.getMessageShort();
      messageFull = versionInfoProperties.getMessageFull();
      branch = versionInfoProperties.getBranch();
    }
    version = Objects.toString(versionInfoProperties.getVersion(), commitId);
    url = versionInfoProperties.getUrlTemplate().replace("{commit}", commitId).replace("{version}", version);
  }

  public static VersionInfo versionInfo(final VersionInfoProperties versionInfoProperties) {
    return versionInfo(versionInfoProperties, null);
  }

  public static VersionInfo versionInfo(final VersionInfoProperties versionInfoProperties, final GitProperties gitProperties) {
    return new VersionInfo(versionInfoProperties, gitProperties);
  }
}
