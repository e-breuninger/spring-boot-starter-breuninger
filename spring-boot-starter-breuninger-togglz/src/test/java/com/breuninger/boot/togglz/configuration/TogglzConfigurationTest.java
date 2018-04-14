package com.breuninger.boot.togglz.configuration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.After;
import org.junit.Test;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TogglzConfigurationTest {

  private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

  @After
  public void close() {
    context.close();
  }

  @Test
  public void shouldRegisterTogglzConsoleServlet() {
    context.register(TogglzConfiguration.class);
    context.register(InMemoryFeatureStateRepositoryConfiguration.class);
    TestPropertyValues.of("breuninger.application.management.base-path=/internal").applyTo(context);
    context.refresh();

    assertThat(context.containsBean("togglzFilter"), is(true));
    assertThat(context.containsBean("featureClassProvider"), is(true));
    assertThat(context.containsBean("userProvider"), is(true));
    assertThat(context.containsBean("togglzConfig"), is(true));
    assertThat(context.containsBean("featureManager"), is(true));
  }
}
