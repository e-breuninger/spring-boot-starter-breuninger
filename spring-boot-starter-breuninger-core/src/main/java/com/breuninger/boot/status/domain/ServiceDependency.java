package com.breuninger.boot.status.domain;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import com.breuninger.boot.annotations.Beta;

import net.jcip.annotations.Immutable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Beta
@Immutable
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class ServiceDependency extends ExternalDependency {

  public static final String TYPE_SERVICE = "service";

  public static final String SUBTYPE_REST = "REST";
  public static final String SUBTYPE_OTHER = "OTHER";

  public static final String AUTH_BASIC = "BASIC";
  public static final String AUTH_DIGEST = "DIGEST";
  public static final String AUTH_HMAC = "HMAC";
  public static final String AUTH_OAUTH = "OAUTH";
  public static final String AUTH_NONE = "NONE";

  private final String url;

  private final List<String> methods;
  private final List<String> mediaTypes;
  private final String authentication;

  ServiceDependency() {
    this(null, null, "", "", "", null, null, null, null, null);
  }

  public ServiceDependency(final String name, final String description, final String url, final String type, final String subtype,
                           final List<String> methods, final List<String> mediaTypes, final String authentication,
                           final Criticality criticality, final Expectations expectations) {
    super(name, description, type, subtype, criticality, expectations);
    this.url = requireNonNull(url, "Parameter 'url' must not be null");
    this.methods = methods != null ? methods : emptyList();
    this.mediaTypes = mediaTypes != null ? mediaTypes : emptyList();
    this.authentication = Objects.toString(authentication, "");
  }
}
