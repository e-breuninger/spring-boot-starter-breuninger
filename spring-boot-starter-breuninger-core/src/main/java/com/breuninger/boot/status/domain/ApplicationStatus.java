package com.breuninger.boot.status.domain;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import net.jcip.annotations.Immutable;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@Immutable
@EqualsAndHashCode
@ToString
public class ApplicationStatus {

  public final ApplicationInfo application;
  public final SystemInfo system;
  public final VersionInfo vcs;
  public final TeamInfo team;
  public final ClusterInfo cluster;
  public final Status status;
  public final List<StatusDetail> statusDetails;

  private ApplicationStatus(final ApplicationInfo application, final ClusterInfo cluster, final SystemInfo system,
                            final VersionInfo vcs, final TeamInfo team, final List<StatusDetail> details) {
    status = details.stream().map(StatusDetail::getStatus).reduce(Status.OK, Status::plus);
    statusDetails = unmodifiableList(new ArrayList<>(details));
    this.application = application;
    this.cluster = cluster;
    this.system = system;
    this.vcs = vcs;
    this.team = team;
  }

  public static ApplicationStatus applicationStatus(final ApplicationInfo applicationInfo, final ClusterInfo clusterInfo,
                                                    final SystemInfo systemInfo, final VersionInfo versionInfo,
                                                    final TeamInfo teamInfo, final List<StatusDetail> details) {
    return new ApplicationStatus(applicationInfo, clusterInfo, systemInfo, versionInfo, teamInfo, details);
  }
}
