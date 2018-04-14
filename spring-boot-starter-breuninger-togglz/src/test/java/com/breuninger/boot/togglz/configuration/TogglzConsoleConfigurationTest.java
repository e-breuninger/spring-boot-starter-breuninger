package com.breuninger.boot.togglz.configuration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.After;
import org.junit.Test;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.breuninger.boot.navigation.NavBarConfiguration;

public class TogglzConsoleConfigurationTest {

  private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

  @After
  public void close() {
    context.close();
  }

  @Test
  public void shouldRegisterTogglzConsoleServlet() {
    context.register(TogglzConsoleConfiguration.class);
    context.register(NavBarConfiguration.class);
    TestPropertyValues.of("breuninger.application.management.base-path=/internal").applyTo(context);
    context.refresh();

    assertThat(context.containsBean("togglzServlet"), is(true));
  }

  @Test
  public void shouldNotRegisterTogglzConsoleServletIfDisabled() {
    context.register(TogglzConsoleConfiguration.class);
    context.register(NavBarConfiguration.class);
    TestPropertyValues.of("breuninger.application.management.base-path=/internal", "breuninger.togglz.console.enabled=false")
      .applyTo(context);
    context.refresh();

    assertThat(context.containsBean("togglzServlet"), is(false));
  }
}
