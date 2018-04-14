package com.breuninger.boot.authentication;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

public class Credentials {

  private final String username;
  private final String password;

  public Credentials(final String username, final String password) {
    this.username = username;
    this.password = password;
  }

  public static Optional<Credentials> readFrom(final HttpServletRequest request) {
    final var authorizationHeader = request.getHeader("Authorization");
    if (!StringUtils.isEmpty(authorizationHeader)) {
      final var credentials = authorizationHeader.substring(6, authorizationHeader.length());
      final var decodedCredentialParts = new String(Base64Utils.decode(credentials.getBytes())).split(":", 2);
      if (!decodedCredentialParts[0].isEmpty() && !decodedCredentialParts[1].isEmpty()) {
        return Optional.of(new Credentials(decodedCredentialParts[0], decodedCredentialParts[1]));
      }
    }
    return Optional.empty();
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}
