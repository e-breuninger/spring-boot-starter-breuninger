package com.breuninger.boot.status.domain;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import static org.assertj.core.api.Assertions.assertThat;

import static com.breuninger.boot.status.domain.ServiceDependency.AUTH_HMAC;
import static com.breuninger.boot.status.domain.ServiceDependency.AUTH_OAUTH;
import static com.breuninger.boot.status.domain.ServiceDependencyBuilder.copyOf;
import static com.breuninger.boot.status.domain.ServiceDependencyBuilder.restServiceDependency;
import static com.breuninger.boot.status.domain.ServiceDependencyBuilder.serviceDependency;

import org.junit.Test;

public class ServiceDependencyBuilderTest {

  @Test
  public void shouldBuildServiceDependency() {
    final var dependency = serviceDependency("http://example.com").withName("name")
      .withDescription("description")
      .withMediaTypes(singletonList("application/json"))
      .withMethods(asList("GET", "HEAD"))
      .withAuthentication(AUTH_HMAC)
      .build();
    assertThat(dependency.getName()).isEqualTo("name");
    assertThat(dependency.getDescription()).isEqualTo("description");
    assertThat(dependency.getAuthentication()).isEqualTo("HMAC");
    assertThat(dependency.getMediaTypes()).contains("application/json");
    assertThat(dependency.getMethods()).contains("GET", "HEAD");
    assertThat(dependency.getType()).isEqualTo("service");
    assertThat(dependency.getSubtype()).isEqualTo("OTHER");
    assertThat(dependency.getUrl()).isEqualTo("http://example.com");
  }

  @Test
  public void shouldBuildRestServiceDependency() {
    final var dependency = restServiceDependency("http://example.com").withName("name")
      .withDescription("description")
      .withMediaTypes(singletonList("application/json"))
      .withAuthentication(AUTH_OAUTH)
      .withMethods(asList("GET", "HEAD"))
      .build();
    assertThat(dependency.getName()).isEqualTo("name");
    assertThat(dependency.getDescription()).isEqualTo("description");
    assertThat(dependency.getAuthentication()).isEqualTo("OAUTH");
    assertThat(dependency.getMediaTypes()).contains("application/json");
    assertThat(dependency.getMethods()).contains("GET", "HEAD");
    assertThat(dependency.getType()).isEqualTo("service");
    assertThat(dependency.getSubtype()).isEqualTo("REST");
    assertThat(dependency.getUrl()).isEqualTo("http://example.com");
  }

  @Test
  public void shouldCopyServiceDependency() {
    final var dependency = serviceDependency("http://example.com").withName("name")
      .withDescription("description")
      .withMediaTypes(singletonList("application/json"))
      .withAuthentication(AUTH_HMAC)
      .withMethods(asList("GET", "HEAD"))
      .withType("some type")
      .withSubtype("some subtype")
      .build();
    assertThat(dependency).isEqualTo(copyOf(dependency).build());
    assertThat(dependency.hashCode()).isEqualTo(copyOf(dependency).build().hashCode());
  }
}
