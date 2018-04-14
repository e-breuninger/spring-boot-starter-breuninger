package com.breuninger.boot.authentication;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import static com.unboundid.ldap.sdk.SearchScope.SUB;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.breuninger.boot.authentication.configuration.LdapProperties;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.ldap.sdk.SearchRequest;

class LdapRoleCheckingRequest extends HttpServletRequestWrapper {

  private static final String CN = "cn";

  private final LDAPInterface ldapInterface;
  private final String userDN;
  private final String roleBaseDN;
  private final List<String> userRoles;

  public LdapRoleCheckingRequest(final HttpServletRequest request, final LDAPInterface ldapInterface, final String userDN,
                                 final LdapProperties ldapProperties) throws LDAPException {
    super(request);
    this.ldapInterface = ldapInterface;
    this.userDN = userDN;
    roleBaseDN = ldapProperties.getRoleBaseDn();
    userRoles = getRoles();
  }

  @Override
  public boolean isUserInRole(final String role) {
    return userRoles.contains(role);
  }

  List<String> getRoles() throws LDAPException {
    final var searchRequest = new SearchRequest(roleBaseDN, SUB, "(uniqueMember=" + userDN + ")", CN);
    final var searchResult = ldapInterface.search(searchRequest);
    return searchResult.getSearchEntries().stream().flatMap(entry -> stream(entry.getAttributeValues("CN"))).collect(toList());
  }
}
