package com.breuninger.boot.authentication.connection;

import java.security.GeneralSecurityException;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;

public interface LdapConnectionFactory {

  LDAPConnection buildLdapConnection() throws GeneralSecurityException, LDAPException;
}
