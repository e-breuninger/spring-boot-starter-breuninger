package com.breuninger.boot.navigation;

import static java.util.Arrays.asList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static com.breuninger.boot.navigation.NavBar.navBar;
import static com.breuninger.boot.navigation.NavBarItem.navBarItem;

import org.junit.Test;

public class NavBarTest {

  @Test
  public void shouldSortNavBarItemsByIndex() {
    final var navBar = navBar(asList(navBarItem(1, "Bar", "/"), navBarItem(0, "Foo", "/")));
    assertThat(navBar.getItems().get(0).getTitle(), is("Foo"));
    assertThat(navBar.getItems().get(1).getTitle(), is("Bar"));
  }

  @Test
  public void shouldSortNavBarItemsByIndexAndTitle() {
    final var navBar = navBar(asList(navBarItem(0, "Foo", "/"), navBarItem(0, "Bar", "/")));
    assertThat(navBar.getItems().get(0).getTitle(), is("Bar"));
    assertThat(navBar.getItems().get(1).getTitle(), is("Foo"));
  }

  @Test
  public void shouldSortNavBarItemsAfterRegistration() {
    final var navBar = navBar(asList(navBarItem(0, "Foo", "/"), navBarItem(1, "Bar", "/")));
    navBar.register(navBarItem(1, "AAARGH", "/"));
    assertThat(navBar.getItems().get(0).getTitle(), is("Foo"));
    assertThat(navBar.getItems().get(1).getTitle(), is("AAARGH"));
    assertThat(navBar.getItems().get(2).getTitle(), is("Bar"));
  }
}
