package com.breuninger.boot.navigation;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.Comparator.comparing;

import java.util.ArrayList;
import java.util.List;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class NavBar {

  private volatile List<NavBarItem> items;

  private NavBar(final List<NavBarItem> items) {
    updateAndSortItems(new ArrayList<>(items));
  }

  public static NavBar emptyNavBar() {
    return new NavBar(emptyList());
  }

  public static NavBar navBar(final List<NavBarItem> items) {
    return new NavBar(items);
  }

  public void register(final NavBarItem item) {
    updateAndSortItems(new ArrayList(items) {{
      add(item);
    }});
  }

  public List<NavBarItem> getItems() {
    return unmodifiableList(items);
  }

  private void updateAndSortItems(final List<NavBarItem> items) {
    items.sort(comparing(NavBarItem::getPosition).thenComparing(NavBarItem::getTitle));
    this.items = unmodifiableList(items);
  }
}
