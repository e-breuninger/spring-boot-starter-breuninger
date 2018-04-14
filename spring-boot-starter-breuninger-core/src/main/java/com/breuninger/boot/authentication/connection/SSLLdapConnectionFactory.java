package com.breuninger.boot.authentication.connection;

import java.security.GeneralSecurityException;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.ssl.SSLUtil;

import com.breuninger.boot.authentication.configuration.LdapProperties;

public class SSLLdapConnectionFactory implements LdapConnectionFactory {

  private static final SSLUtil SSL_UTIL = new SSLUtil();
  private final LdapProperties ldapProperties;

  public SSLLdapConnectionFactory(final LdapProperties ldapProperties) {
    this.ldapProperties = ldapProperties;
  }

  @Override
  public LDAPConnection buildLdapConnection() throws GeneralSecurityException, LDAPException {
    final var sslSocketFactory = SSL_UTIL.createSSLSocketFactory();
    return new LDAPConnection(sslSocketFactory, ldapProperties.getHost(), ldapProperties.getPort());
  }
}
