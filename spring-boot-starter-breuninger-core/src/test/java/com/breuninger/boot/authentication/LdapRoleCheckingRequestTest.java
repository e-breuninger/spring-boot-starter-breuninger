package com.breuninger.boot.authentication;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.breuninger.boot.authentication.configuration.EncryptionType.START_TLS;
import static com.breuninger.boot.authentication.configuration.LdapProperties.ldapProperties;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;

import com.breuninger.boot.authentication.configuration.LdapProperties;

public class LdapRoleCheckingRequestTest {

  @Test
  public void shouldReturnUserRoles() throws LDAPException {
    final var mockRequest = mock(HttpServletRequest.class);
    final var ldap = someLdapInterfaceReturning("foo");

    final var request = new LdapRoleCheckingRequest(mockRequest, ldap, "uid=test", someLdapProperties());

    assertThat(request.getRoles()).contains("foo");
  }

  @Test
  public void shouldCheckUserRoles() throws LDAPException {
    final var mockRequest = mock(HttpServletRequest.class);
    final var ldap = someLdapInterfaceReturning("foo", "bar");

    final var request = new LdapRoleCheckingRequest(mockRequest, ldap, "uid=test", someLdapProperties());

    assertThat(request.isUserInRole("foo")).isEqualTo(true);
    assertThat(request.isUserInRole("foobar")).isEqualTo(false);
  }

  private LdapProperties someLdapProperties() {
    return ldapProperties("", 389, singletonList("someBaseDn"), "someRoleBaseDn", "someRdnIdentifier", "/internal", START_TLS);
  }

  private LDAPInterface someLdapInterfaceReturning(final String... roles) throws LDAPSearchException {
    final var ldap = mock(LDAPInterface.class);
    final var entries = singletonList(new SearchResultEntry("", singleton(new Attribute("cn", roles))));
    final var searchResult = new SearchResult(0, null, null, null, null, entries, emptyList(), 1, 0, null);
    when(ldap.search(any(SearchRequest.class))).thenReturn(searchResult);
    return ldap;
  }
}
