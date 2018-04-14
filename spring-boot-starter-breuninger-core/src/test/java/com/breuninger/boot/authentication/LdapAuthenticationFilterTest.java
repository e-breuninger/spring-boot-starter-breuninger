package com.breuninger.boot.authentication;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.WWW_AUTHENTICATE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import static com.unboundid.ldap.sdk.ResultCode.AUTHORIZATION_DENIED;
import static com.unboundid.ldap.sdk.ResultCode.SERVER_DOWN;
import static com.unboundid.ldap.sdk.ResultCode.SUCCESS;

import static com.breuninger.boot.authentication.configuration.EncryptionType.START_TLS;
import static com.breuninger.boot.authentication.configuration.LdapProperties.ldapProperties;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Base64Utils;

import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPBindException;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;

import com.breuninger.boot.authentication.connection.LdapConnectionFactory;

public class LdapAuthenticationFilterTest {

  private static final String WHITELISTED_PATH = "/internal/health";

  private LdapAuthenticationFilter testee;
  private HttpServletResponse response;
  private LdapConnectionFactory ldapConnectionFactory;

  @Before
  public void setUp() {
    ldapConnectionFactory = mock(LdapConnectionFactory.class);
    response = mock(HttpServletResponse.class);

    testee = new LdapAuthenticationFilter(
      ldapProperties("someHost", 389, singletonList("someBaseDn"), null, "someRdnIdentifier", "/internal", START_TLS,
        WHITELISTED_PATH), ldapConnectionFactory);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldFailToStartIfHostIsNotConfigured() {
    new LdapAuthenticationFilter(
      ldapProperties("", 389, singletonList("someBaseDn"), null, "someRdnIdentifier", "/internal", START_TLS),
      ldapConnectionFactory);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldFailToStartIfBaseDnIsNotConfigured() {
    new LdapAuthenticationFilter(
      ldapProperties("someHost", 389, singletonList(""), null, "someRdnIdentifier", "/internal", START_TLS),
      ldapConnectionFactory);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldFailToStartIfRdnIdentifierIsNotConfigured() {
    new LdapAuthenticationFilter(ldapProperties("someHost", 389, singletonList("someBaseDn"), null, "", "/internal", START_TLS),
      ldapConnectionFactory);
  }

  @Test
  public void shouldFailToStartIfAuthorizationHeaderIsMissing() throws Exception {
    testee.doFilter(requestWithoutAuthorizationHeader(), response, mock(FilterChain.class));
    assertUnauthorized();
  }

  @Test
  public void shouldBeUnauthenticatedIfLdapConnectionFails() throws Exception {
    final var ldapConnection = someLdapConnectionReturning(SERVER_DOWN);
    when(ldapConnectionFactory.buildLdapConnection()).thenReturn(ldapConnection);

    testee.doFilter(requestWithAuthorizationHeader(), response, mock(FilterChain.class));
    assertUnauthorized();
  }

  @Test
  public void shouldNotApplyFilterToWhitelistedEndpoint() throws Exception {
    final var request = requestWithoutAuthorizationHeader();
    when(request.getServletPath()).thenReturn(WHITELISTED_PATH + "/etc");

    final var filterChain = mock(FilterChain.class);
    testee.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void shouldNotApplyFilterToInternalJavascript() throws Exception {
    final var request = requestWithoutAuthorizationHeader();
    when(request.getServletPath()).thenReturn("/internal/js/foo.js");

    final var filterChain = mock(FilterChain.class);
    testee.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void shouldApplyFilterToAuthenticatedUser()
    throws IOException, ServletException, GeneralSecurityException, LDAPException {
    final var ldapProperties = ldapProperties("someHost", 389, singletonList("someBaseDn"), null, "someRdnIdentifier",
      "/internal", START_TLS, WHITELISTED_PATH);
    final var connectionFactory = mock(LdapConnectionFactory.class);
    final var ldapConnection = someLdapConnectionReturning(SUCCESS);
    when(connectionFactory.buildLdapConnection()).thenReturn(ldapConnection);
    testee = new LdapAuthenticationFilter(ldapProperties, connectionFactory);
    final var request = requestWithAuthorizationHeader();
    when(request.getServletPath()).thenReturn("/foo");
    final var filterChain = mock(FilterChain.class);
    testee.doFilter(request, response, filterChain);
    verify(filterChain).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));
  }

  @Test
  public void shouldApplyFilterToAuthenticatedUserWithAdditionallyConfiguredBaseDn()
    throws IOException, ServletException, GeneralSecurityException, LDAPException {
    // given
    final var ldapProperties = ldapProperties("someHost", 389, asList("exceptionBaseDn", "successBaseDn"), null,
      "someRdnIdentifier", "/internal", START_TLS, WHITELISTED_PATH);
    final var connectionFactory = mock(LdapConnectionFactory.class);
    final var ldapConnection = someLdapConnectionReturningSuccessOrThrowingBindException("successBaseDn",
      "exceptionBaseDn");
    when(connectionFactory.buildLdapConnection()).thenReturn(ldapConnection);
    testee = new LdapAuthenticationFilter(ldapProperties, connectionFactory);
    // when
    final var request = requestWithAuthorizationHeader();
    when(request.getServletPath()).thenReturn("/foo");
    final var filterChain = mock(FilterChain.class);
    testee.doFilter(request, response, filterChain);
    // then
    verify(ldapConnection).bind(contains("exceptionBaseDn"), anyString());
    verify(ldapConnection).bind(contains("successBaseDn"), anyString());
    verify(filterChain).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));
  }

  @Test
  public void shouldNotApplyFilterToNotAuthenticatedUser()
    throws IOException, ServletException, GeneralSecurityException, LDAPException {
    final var ldapProperties = ldapProperties("someHost", 389, singletonList("someBaseDn"), null, "someRdnIdentifier",
      "/internal", START_TLS, WHITELISTED_PATH);
    final var connectionFactory = mock(LdapConnectionFactory.class);
    final var ldapConnection = someLdapConnectionReturning(AUTHORIZATION_DENIED);
    when(connectionFactory.buildLdapConnection()).thenReturn(ldapConnection);
    testee = new LdapAuthenticationFilter(ldapProperties, connectionFactory);
    final var request = requestWithAuthorizationHeader();
    when(request.getServletPath()).thenReturn("/foo");
    final var filterChain = mock(FilterChain.class);
    testee.doFilter(request, response, filterChain);
    verify(filterChain, never()).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));
  }

  @Test
  public void shouldAuthenticateUser() throws LDAPException {
    final var authenticated = testee.authenticate(someLdapConnectionReturning(SUCCESS), "user", "password");
    assertThat(authenticated).isEqualTo(true);
  }

  @Test
  public void shouldNotAuthenticateUser() throws LDAPException {
    final var authenticated = testee.authenticate(someLdapConnectionReturning(AUTHORIZATION_DENIED), "user", "password");
    assertThat(authenticated).isEqualTo(false);
  }

  @Test
  public void shouldBuildUserDnFromCredentials() {
    final var userDn = testee.userDnFrom(new Credentials("user", "password"), "someBaseDn");
    assertThat(userDn).isEqualTo("someRdnIdentifier=user,someBaseDn");
  }

  private HttpServletRequest requestWithoutAuthorizationHeader() {
    final var request = mock(HttpServletRequest.class);
    when(request.getServletPath()).thenReturn("/internal");
    return request;
  }

  private HttpServletRequest requestWithAuthorizationHeader() {
    final var request = mock(HttpServletRequest.class);
    when(request.getHeader(AUTHORIZATION)).thenReturn(
      "Basic " + Base64Utils.encodeToString("someUsername:somePassword".getBytes()));
    when(request.getServletPath()).thenReturn("/internal");
    return request;
  }

  private void assertUnauthorized() {
    verify(response).setStatus(UNAUTHORIZED.value());
    verify(response).addHeader(WWW_AUTHENTICATE, "Basic realm=Authorization Required");
  }

  private LDAPConnection someLdapConnectionReturning(final ResultCode resultCode) throws LDAPException {
    final var ldap = mock(LDAPConnection.class);
    final var mockBindResult = mock(BindResult.class);
    when(mockBindResult.getResultCode()).thenReturn(resultCode);
    when(ldap.bind(anyString(), anyString())).thenReturn(mockBindResult);
    return ldap;
  }

  private LDAPConnection someLdapConnectionReturningSuccessOrThrowingBindException(final String bindDnSuccess, final String bindDnException)
    throws LDAPException {
    final var ldap = mock(LDAPConnection.class);

    final var mockBindResultSuccess = mock(BindResult.class);
    when(mockBindResultSuccess.getResultCode()).thenReturn(SUCCESS);
    when(ldap.bind(contains(bindDnSuccess), anyString())).thenReturn(mockBindResultSuccess);

    final var mockBindResultInvalid = mock(BindResult.class);
    when(mockBindResultInvalid.getResultCode()).thenReturn(ResultCode.INVALID_CREDENTIALS);
    final var mockBindException = mock(LDAPBindException.class);
    when(mockBindException.getBindResult()).thenReturn(mockBindResultInvalid);
    when(ldap.bind(contains(bindDnException), anyString())).thenThrow(mockBindException);
    return ldap;
  }
}
