package com.breuninger.boot.authentication;

import static java.lang.String.format;

import static org.springframework.http.HttpHeaders.WWW_AUTHENTICATE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import static com.breuninger.boot.authentication.Credentials.readFrom;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.breuninger.boot.authentication.configuration.LdapProperties;
import com.breuninger.boot.authentication.connection.LdapConnectionFactory;
import com.unboundid.ldap.sdk.LDAPBindException;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LdapAuthenticationFilter extends OncePerRequestFilter {

  private static final String INTERNAL_JS_PATH = "/internal/js/";

  private final LdapProperties ldapProperties;
  private final LdapConnectionFactory ldapConnectionFactory;

  public LdapAuthenticationFilter(final LdapProperties ldapProperties, final LdapConnectionFactory ldapConnectionFactory) {
    if (!ldapProperties.isValid()) {
      throw new IllegalStateException("Invalid LdapProperties");
    }
    this.ldapProperties = ldapProperties;
    this.ldapConnectionFactory = ldapConnectionFactory;
  }

  @Override
  protected boolean shouldNotFilter(final HttpServletRequest request) {
    final var servletPath = request.getServletPath();
    // TODO remove INTERNAL_JS_PATH and add to whitelist
    return servletPath.startsWith(INTERNAL_JS_PATH) ||
      ldapProperties.getWhitelistedPaths().stream().anyMatch(servletPath::startsWith);
  }

  @Override
  protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                  final FilterChain filterChain) throws ServletException, IOException {
    final var optionalCredentials = readFrom(request);
    if (optionalCredentials.isPresent()) {
      final var authRequest = tryToGetAuthenticatedRequest(request, optionalCredentials.get());
      if (authRequest.isPresent()) {
        filterChain.doFilter(authRequest.get(), response);
      } else {
        unauthorized(response);
      }
    } else {
      unauthorized(response);
    }
  }

  private Optional<HttpServletRequest> tryToGetAuthenticatedRequest(final HttpServletRequest request,
                                                                    final Credentials credentials) {
    try (final var ldap = ldapConnectionFactory.buildLdapConnection()) {

      for (final var baseDN : ldapProperties.getBaseDn()) {
        final var userDN = userDnFrom(credentials, baseDN);
        try {
          if (authenticate(ldap, userDN, credentials.getPassword())) {
            return ldapProperties.getRoleBaseDn() != null ?
              Optional.of(new LdapRoleCheckingRequest(request, ldap, userDN, ldapProperties)) :
              Optional.of(request);
          }
        } catch (final LDAPBindException e) {
          LOG.debug("LDAPBindException for userDN: {}", userDN);
        }
      }
      LOG.warn("Could not bind to LDAP: {}", credentials.getUsername());
    } catch (final LDAPException | GeneralSecurityException e) {
      LOG.warn("Authentication error: ", e);
    }
    return Optional.empty();
  }

  private void unauthorized(final HttpServletResponse httpResponse) {
    httpResponse.addHeader(WWW_AUTHENTICATE, "Basic realm=Authorization Required");
    httpResponse.setStatus(UNAUTHORIZED.value());
  }

  String userDnFrom(final Credentials credentials, final String baseDN) {
    return format("%s=%s,%s", ldapProperties.getRdnIdentifier(), credentials.getUsername(), baseDN);
  }

  boolean authenticate(final LDAPConnection ldap, final String userDN, final String password) throws LDAPException {
    final var bindResult = ldap.bind(userDN, password);
    if (bindResult.getResultCode().equals(ResultCode.SUCCESS)) {
      LOG.info("Login successful: " + userDN);
      return true;
    } else {
      LOG.warn("Access denied: " + userDN);
      return false;
    }
  }
}
