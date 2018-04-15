package com.breuninger.boot.togglz;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.hamcrest.core.Is.is;

import org.junit.Before;
import org.junit.Test;
import org.togglz.core.manager.TogglzConfig;
import org.togglz.core.repository.cache.CachingStateRepository;

import com.breuninger.boot.testsupport.applicationdriver.SpringTestBase;

public class DefaultTogglzConfigTest extends SpringTestBase {

  private TogglzConfig togglzConfig;

  @Before
  public void setUp() {
    togglzConfig = applicationContext().getBean(TogglzConfig.class);
  }

  @Test
  public void shouldCreateTogglzConfigBySpring() {
    assertThat(togglzConfig, is(not(nullValue())));
    assertThat(togglzConfig.getFeatureClass(), typeCompatibleWith(TestFeatures.class));
    assertThat(togglzConfig.getStateRepository(), is(not(nullValue())));
    assertThat(togglzConfig.getStateRepository(), is(instanceOf(CachingStateRepository.class)));
    assertThat(togglzConfig.getUserProvider(), is(not(nullValue())));
  }

  @Test
  public void shouldProvideToggleStateWhichIsActiveByDefaultInTests() {
    assertThat(TestFeatures.TEST_FEATURE.isActive(), is(true));
  }
}
