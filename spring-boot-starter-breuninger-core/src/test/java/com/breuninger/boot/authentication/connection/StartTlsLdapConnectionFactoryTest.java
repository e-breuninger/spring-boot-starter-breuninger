package com.breuninger.boot.authentication.connection;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.GeneralSecurityException;

import org.junit.Test;

import com.unboundid.ldap.sdk.LDAPException;

import com.breuninger.boot.authentication.configuration.LdapProperties;

public class StartTlsLdapConnectionFactoryTest {

  @Test(expected = LDAPException.class)
  public void shouldTryToBuildLdapConnection() throws GeneralSecurityException, LDAPException {
    final var properties = mock(LdapProperties.class);
    when(properties.getHost()).thenReturn("foo");
    when(properties.getPort()).thenReturn(42);
    new StartTlsLdapConnectionFactory(properties).buildLdapConnection();
  }
}
