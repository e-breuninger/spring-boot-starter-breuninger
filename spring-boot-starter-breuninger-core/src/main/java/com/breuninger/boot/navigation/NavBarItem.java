package com.breuninger.boot.navigation;

import static java.lang.Integer.MAX_VALUE;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NavBarItem {

  private final int position;
  private final String title;
  private final String link;

  public static NavBarItem navBarItem(final int position, final String title, final String link) {
    return new NavBarItem(position, title, link);
  }

  public static int top() {
    return 0;
  }

  public static int bottom() {
    return MAX_VALUE;
  }
}
