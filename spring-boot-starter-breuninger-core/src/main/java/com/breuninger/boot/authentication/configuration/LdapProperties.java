package com.breuninger.boot.authentication.configuration;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import static org.springframework.util.StringUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConfigurationProperties(prefix = "breuninger.ldap")
@Validated
@Getter
@Setter
public class LdapProperties {

  private boolean enabled;
  private @NotEmpty String host;
  private @Min(1) int port = 389;
  private @NotEmpty List<String> baseDn;
  private String roleBaseDn;
  private @NotEmpty String rdnIdentifier;
  private String prefix = "/internal";
  // TODO remove default internal/health
  private List<String> whitelistedPaths = singletonList("/internal/health");
  private EncryptionType encryptionType = EncryptionType.START_TLS;

  public static LdapProperties ldapProperties(final String host, final int port, final List<String> baseDn,
                                              final String roleBaseDn, final String rdnIdentifier, final String prefix,
                                              final EncryptionType encryptionType, final String... whitelistedPaths) {
    final var ldap = new LdapProperties();
    ldap.setEnabled(true);
    ldap.setHost(host);
    ldap.setPort(port);
    ldap.setBaseDn(baseDn);
    ldap.setRoleBaseDn(roleBaseDn);
    ldap.setRdnIdentifier(rdnIdentifier);
    ldap.setPrefix(prefix);
    ldap.setEncryptionType(encryptionType);
    ldap.setWhitelistedPaths(asList(whitelistedPaths));
    return ldap;
  }

  public boolean isValid() {
    if (isEmpty(host)) {
      LOG.error("host is undefined");
    } else if (baseDn == null || baseDn.isEmpty() || hasEmptyElements(baseDn)) {
      LOG.error("baseDn is undefined");
    } else if (isEmpty(rdnIdentifier)) {
      LOG.error("rdnIdentifier is undefined");
    } else {
      return true;
    }
    return false;
  }

  private boolean hasEmptyElements(final List<String> listOfStrings) {
    final List<String> listCopy = new ArrayList<>(listOfStrings);
    return listCopy.removeAll(asList("", null));
  }
}
