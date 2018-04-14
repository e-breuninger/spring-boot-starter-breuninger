package com.breuninger.boot.authentication.configuration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.breuninger.boot.authentication.connection.SSLLdapConnectionFactory;
import com.breuninger.boot.authentication.connection.StartTlsLdapConnectionFactory;

public class LdapConfigurationTest {

  private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

  @After
  public void close() {
    context.close();
  }

  @Test
  public void shouldRegisterLdapFilter() {
    context.register(EnableAutoConfig.class);
    TestPropertyValues.of("breuninger.application.management.base-path=/internal")
      .and("breuninger.ldap.enabled=true")
      .and("breuninger.ldap.host=localhost")
      .and("breuninger.ldap.base-dn=test-dn")
      .and("breuninger.ldap.rdn-identifier=test-rdn")
      .applyTo(context);
    context.refresh();

    assertThat(context.containsBean("ldapAuthenticationFilter"), is(true));
  }

  @Test
  public void shouldNotRegisterLdapFilterIfDisabled() {
    context.register(EnableAutoConfig.class);
    TestPropertyValues.of("breuninger.application.management.base-path=/internal").and("breuninger.ldap.enabled=false").applyTo(context);
    context.refresh();

    assertThat(context.containsBean("ldapAuthenticationFilter"), is(false));
  }

  @Test(expected = UnsatisfiedDependencyException.class)
  public void shouldValidateProperties() {
    context.register(EnableAutoConfig.class);
    TestPropertyValues.of("breuninger.application.management.base-path=/internal").and("breuninger.ldap.enabled=true").applyTo(context);

    context.refresh();
  }

  @Test
  public void shouldUseSSLEncryptionIfConfigured() {
    context.register(EnableAutoConfig.class);
    TestPropertyValues.of("breuninger.application.management.base-path=/internal")
      .and("breuninger.ldap.enabled=true")
      .and("breuninger.ldap.encryptionType=SSL")
      .and("breuninger.ldap.host=localhost")
      .and("breuninger.ldap.base-dn=test-dn")
      .and("breuninger.ldap.rdn-identifier=test-rdn")
      .applyTo(context);
    context.refresh();

    assertThat(context.getBean("ldapConnectionFactory").getClass().getSimpleName(),
      is(SSLLdapConnectionFactory.class.getSimpleName()));
  }

  @Test
  public void shouldUseStartTLSEncryptionIfConfigured() {
    context.register(EnableAutoConfig.class);
    TestPropertyValues.of("breuninger.application.management.base-path=/internal")
      .and("breuninger.ldap.enabled=true")
      .and("breuninger.ldap.encryptionType=StartTLS")
      .and("breuninger.ldap.host=localhost")
      .and("breuninger.ldap.base-dn=test-dn")
      .and("breuninger.ldap.rdn-identifier=test-rdn")
      .applyTo(context);
    context.refresh();

    assertThat(context.getBean("ldapConnectionFactory").getClass().getSimpleName(),
      is(StartTlsLdapConnectionFactory.class.getSimpleName()));
  }

  @Test
  public void shouldUseStartTLSEncryptionAsDefault() {
    context.register(EnableAutoConfig.class);
    TestPropertyValues.of("breuninger.application.management.base-path=/internal")
      .and("breuninger.ldap.enabled=true")
      .and("breuninger.ldap.host=localhost")
      .and("breuninger.ldap.base-dn=test-dn")
      .and("breuninger.ldap.rdn-identifier=test-rdn")
      .applyTo(context);
    context.refresh();

    assertThat(context.getBean("ldapConnectionFactory").getClass().getSimpleName(),
      is(StartTlsLdapConnectionFactory.class.getSimpleName()));
  }

  @ImportAutoConfiguration(LdapConfiguration.class)
  private static class EnableAutoConfig {
  }
}
