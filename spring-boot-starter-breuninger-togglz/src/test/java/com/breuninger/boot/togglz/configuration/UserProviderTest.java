package com.breuninger.boot.togglz.configuration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.springframework.util.Base64Utils;
import org.togglz.servlet.util.HttpServletRequestHolder;

public class UserProviderTest {

  @Test
  public void shouldReturnAuthenticatedUser() {

    // given
    final var userProvider = new TogglzConfiguration().userProvider();

    final var mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getHeader("Authorization")).thenReturn("Basic " + Base64Utils.encodeToString("testuser:passwd".getBytes()));

    HttpServletRequestHolder.bind(mockRequest);

    // when
    final var currentUser = userProvider.getCurrentUser();
    // then
    assertThat(currentUser.getName(), is("testuser"));
  }
}
