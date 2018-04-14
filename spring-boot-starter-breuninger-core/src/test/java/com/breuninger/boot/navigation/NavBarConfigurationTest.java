package com.breuninger.boot.navigation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import static com.breuninger.boot.navigation.NavBarItem.top;

import org.junit.After;
import org.junit.Test;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class NavBarConfigurationTest {

  private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

  @After
  public void close() {
    context.close();
  }

  @Test
  public void shouldHaveRightNavBar() {
    TestPropertyValues.of("breuninger.application.management.base-path=/internal").applyTo(context);
    context.register(NavBarConfiguration.class);
    context.refresh();

    final var rightNavBar = context.getBean("rightNavBar", NavBar.class);
    assertThat(rightNavBar.getItems(), hasSize(1));

    final var item = rightNavBar.getItems().get(0);
    assertThat(item.getLink(), is("/internal/status"));
    assertThat(item.getTitle(), is("Status"));
    assertThat(item.getPosition(), is(top()));
  }

  @Test
  public void shouldHaveEmptyMainNavBar() {
    context.register(NavBarConfiguration.class);
    context.refresh();

    final var mainNavBar = context.getBean("mainNavBar", NavBar.class);
    assertThat(mainNavBar.getItems(), hasSize(0));
  }
}

