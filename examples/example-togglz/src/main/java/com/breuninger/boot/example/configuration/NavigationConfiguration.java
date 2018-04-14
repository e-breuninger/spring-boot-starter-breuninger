package com.breuninger.boot.example.configuration;

import static com.breuninger.boot.navigation.NavBarItem.navBarItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.breuninger.boot.configuration.BreuningerApplicationProperties;
import com.breuninger.boot.navigation.NavBar;
import com.breuninger.boot.togglz.configuration.TogglzProperties;

@Component
@EnableConfigurationProperties({TogglzProperties.class, BreuningerApplicationProperties.class})
public class NavigationConfiguration {

  @Autowired
  public NavigationConfiguration(final NavBar mainNavBar, final TogglzProperties togglzProperties,
                                 final BreuningerApplicationProperties breuningerApplicationProperties) {
    mainNavBar.register(navBarItem(0, "Home", "/"));
    if (togglzProperties.getConsole().isEnabled()) {
      mainNavBar.register(navBarItem(1, "Feature Toggles",
        String.format("%s/toggles/console/index", breuningerApplicationProperties.getManagement().getBasePath())));
    }
  }
}
