package com.breuninger.boot.authentication.connection;

import java.security.GeneralSecurityException;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.extensions.StartTLSExtendedRequest;
import com.unboundid.util.ssl.SSLUtil;

import com.breuninger.boot.authentication.configuration.LdapProperties;

public class StartTlsLdapConnectionFactory implements LdapConnectionFactory {

  private static final SSLUtil SSL_UTIL = new SSLUtil();
  private final LdapProperties ldapProperties;

  public StartTlsLdapConnectionFactory(final LdapProperties ldapProperties) {
    this.ldapProperties = ldapProperties;
  }

  @Override
  public LDAPConnection buildLdapConnection() throws GeneralSecurityException, LDAPException {
    final var ldapConnection = new LDAPConnection(ldapProperties.getHost(), ldapProperties.getPort());
    ldapConnection.processExtendedOperation(new StartTLSExtendedRequest(SSL_UTIL.createSSLContext()));
    return ldapConnection;
  }
}
