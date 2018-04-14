package com.breuninger.boot.status.controller;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.breuninger.boot.status.domain.ApplicationStatus;
import com.breuninger.boot.status.domain.ClusterInfo;
import com.breuninger.boot.status.domain.Criticality;
import com.breuninger.boot.status.domain.Datasource;
import com.breuninger.boot.status.domain.DatasourceDependency;
import com.breuninger.boot.status.domain.Expectations;
import com.breuninger.boot.status.domain.ExternalDependency;
import com.breuninger.boot.status.domain.Link;
import com.breuninger.boot.status.domain.ServiceDependency;
import com.breuninger.boot.status.domain.Status;
import com.breuninger.boot.status.domain.StatusDetail;
import com.breuninger.boot.status.domain.SystemInfo;
import com.breuninger.boot.status.domain.TeamInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_EMPTY)
public class StatusRepresentation {

  private static final Pattern STATUS_DETAIL_JSON_SEPARATOR_PATTERN = Pattern.compile("\\s(.)");

  public final ApplicationRepresentation application;
  public final ClusterInfo cluster;
  public final SystemInfo system;
  public final TeamInfo team;
  public final Criticality criticality;
  public final List<DependencyRepresentation> dependencies;

  private StatusRepresentation(final ApplicationStatus applicationStatus, final Criticality criticality,
                               final List<ExternalDependency> dependencies) {
    application = new ApplicationRepresentation(applicationStatus);
    system = applicationStatus.system;
    team = applicationStatus.team;
    cluster = applicationStatus.cluster.isEnabled() ? applicationStatus.cluster : null;
    this.criticality = criticality;
    this.dependencies = dependencies != null ?
      dependencies.stream().sorted(comparing(ExternalDependency::getName)).map(DependencyRepresentation::new).collect(toList()) :
      emptyList();
  }

  public static StatusRepresentation statusRepresentationOf(final ApplicationStatus status) {
    return new StatusRepresentation(status, null, null);
  }

  public static StatusRepresentation statusRepresentationOf(final ApplicationStatus status, final Criticality criticality,
                                                            final List<ExternalDependency> dependencies) {
    return new StatusRepresentation(status, criticality, dependencies);
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonInclude(NON_EMPTY)
  static class DependencyRepresentation {
    public final Expectations expectations;
    public final Criticality criticality;
    public final String name;
    public final String description;
    public final String type;
    public List<String> datasources = emptyList();
    public String url = "";
    public String methods = "";
    public List<String> mediatypes = emptyList();
    public String authentication = "";

    public DependencyRepresentation(final ExternalDependency dependency) {
      name = dependency.getName();
      description = dependency.getDescription();
      type = dependency.getType() + "/" + dependency.getSubtype();
      criticality = dependency.getCriticality();
      expectations = dependency.getExpectations();
      if (dependency instanceof ServiceDependency) {
        final var serviceDependency = (ServiceDependency) dependency;
        url = serviceDependency.getUrl();
        methods = valueOf(serviceDependency.getMethods());
        mediatypes = serviceDependency.getMediaTypes();
        authentication = serviceDependency.getAuthentication();
      } else {
        final var datasources = ((DatasourceDependency) dependency).getDatasources();
        this.datasources = datasources.stream().map(Datasource::toString).collect(toList());
      }
    }

    <T> String valueOf(final List<T> value) {
      if (value != null) {
        return value.stream().map(Object::toString).collect(joining(", "));
      }
      return "";
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  static class ApplicationRepresentation {
    public String name;
    public String title;
    public String description;
    public String group;
    public String environment;
    public String version;
    public String commit;
    public String vcsUrl;
    public Status status;
    public Map<String, ?> statusDetails;

    public ApplicationRepresentation() {
    }

    private ApplicationRepresentation(final ApplicationStatus applicationStatus) {
      name = applicationStatus.application.name;
      title = applicationStatus.application.title;
      description = applicationStatus.application.description;
      group = applicationStatus.application.group;
      environment = applicationStatus.application.environment;
      version = applicationStatus.vcs.version;
      commit = applicationStatus.vcs.commitId;
      vcsUrl = applicationStatus.vcs.url;
      status = applicationStatus.status;
      statusDetails = statusDetailsOf(applicationStatus.statusDetails);
    }

    private static String toCamelCase(final String name) {
      final var matcher = STATUS_DETAIL_JSON_SEPARATOR_PATTERN.matcher(name);
      final var sb = new StringBuffer();
      while (matcher.find()) {
        matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
      }
      matcher.appendTail(sb);
      final var s = sb.toString();
      return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    private Map<String, ?> statusDetailsOf(final List<StatusDetail> statusDetails) {
      final Map<String, Object> map = new LinkedHashMap<>();
      for (final var entry : statusDetails) {
        final var links = toLinks(entry.getLinks());
        map.put(toCamelCase(entry.getName()), new LinkedHashMap<String, Object>() {{
          put("status", entry.getStatus().name());
          put("message", entry.getMessage());
          put("links", links);
          putAll(entry.getDetails()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(entry -> toCamelCase(entry.getKey()), Map.Entry::getValue)));
        }});
      }
      return map;
    }

    private List<Map<String, String>> toLinks(final List<Link> links) {
      final List<Map<String, String>> result = new ArrayList<>();
      links.forEach(link -> result.add(new LinkedHashMap() {{
        put("rel", link.rel);
        put("href", link.href.startsWith("http") ? link.href : fromCurrentContextPath().path(link.href).build().toString());
        put("title", link.title);
      }}));
      return result;
    }
  }
}
