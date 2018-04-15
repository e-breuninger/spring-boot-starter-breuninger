package com.breuninger.boot.authentication;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.util.Base64Utils.encodeToString;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class CredentialsTest {

  @Mock
  private HttpServletRequest httpServletRequest;

  private void mockHttpServletRequestWithAuthentication(final String authString) {
    when(httpServletRequest.getHeader("Authorization")).thenReturn("Basic " + encodeToString(authString.getBytes()));
  }

  @Before
  public void setUp() {
    initMocks(this);
  }

  @Test
  public void shouldBeAbleToReadCredentialsFromRequest() {
    // given
    mockHttpServletRequestWithAuthentication("someUsername:somePassword");

    // when
    final var credentials = Credentials.readFrom(httpServletRequest);

    // then
    assertThat(credentials.isPresent(), is(true));
    assertThat(credentials.get().getUsername(), is("someUsername"));
    assertThat(credentials.get().getPassword(), is("somePassword"));
  }

  @Test
  public void shouldReturnEmptyCredentialsIfHeaderDoesNotExist() {
    // when
    final var credentials = Credentials.readFrom(httpServletRequest);

    // then
    assertThat(credentials.isPresent(), is(false));
  }

  @Test
  public void shouldReturnEmptyCredentialsIfPasswordNotSet() {
    // given
    mockHttpServletRequestWithAuthentication("someUsername:");

    // when
    final var credentials = Credentials.readFrom(httpServletRequest);

    // then
    assertThat(credentials.isPresent(), is(false));
  }

  @Test
  public void shouldReturnEmptyCredentialsIfUsernameNotSet() {
    // given
    mockHttpServletRequestWithAuthentication(":password");

    // when
    final var credentials = Credentials.readFrom(httpServletRequest);

    // then
    assertThat(credentials.isPresent(), is(false));
  }

  @Test
  public void shouldReturnCorrectCredentialsIfPasswordContainsColons() {
    // given
    mockHttpServletRequestWithAuthentication("user:pass:word");

    // when
    final var credentials = Credentials.readFrom(httpServletRequest);

    // then
    assertThat(credentials.isPresent(), is(true));
    assertThat(credentials.get().getUsername(), is("user"));
    assertThat(credentials.get().getPassword(), is("pass:word"));
  }
}
