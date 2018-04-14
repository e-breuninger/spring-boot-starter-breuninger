package com.breuninger.boot.togglz.configuration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.breuninger.boot.authentication.configuration.LdapConfiguration;

public class TogglzLdapConfigurationTest {

  private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

  @After
  public void close() {
    context.close();
  }

  @Test
  public void shouldRegisterLdapFilter() {
    context.register(EnableAutoConfig.class);
    TestPropertyValues.of("breuninger.application.management.base-path=/internal", "breuninger.ldap.enabled=true",
      "breuninger.ldap.host=localhost", "breuninger.ldap.base-dn=test-dn", "breuninger.ldap.rdn-identifier=test-rdn").applyTo(context);
    context.refresh();

    assertThat(context.containsBean("ldapAuthenticationFilter"), is(true));
  }

  @Test
  public void shouldNotRegisterLdapFilterIfDisabled() {
    context.register(EnableAutoConfig.class);
    TestPropertyValues.of("breuninger.application.management.base-path=/internal", "breuninger.ldap.enabled=false").applyTo(context);
    context.refresh();

    assertThat(context.containsBean("ldapAuthenticationFilter"), is(false));
  }

  @Test(expected = UnsatisfiedDependencyException.class)
  public void shouldValidateProperties() {
    context.register(EnableAutoConfig.class);
    TestPropertyValues.of("breuninger.application.management.base-path=/internal", "breuninger.ldap.enabled=true").applyTo(context);

    context.refresh();
  }

  @ImportAutoConfiguration(LdapConfiguration.class)
  private static class EnableAutoConfig {
  }
}
