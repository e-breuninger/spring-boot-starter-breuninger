package com.breuninger.boot.status.domain;

import static com.breuninger.boot.configuration.BreuningerApplicationProperties.breuningerApplicationProperties;
import static com.breuninger.boot.status.domain.ApplicationInfo.applicationInfo;

import org.junit.Test;

public class ApplicationInfoTest {

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailToConstructWithOutName() {
    // given
    final var applicationInfo = applicationInfo("", breuningerApplicationProperties("", "", "", ""));
  }
}
