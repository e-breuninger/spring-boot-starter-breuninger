package com.breuninger.boot.example.configuration;

import static java.lang.String.format;

import static com.breuninger.boot.navigation.NavBarItem.navBarItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.breuninger.boot.configuration.BreuningerApplicationProperties;
import com.breuninger.boot.navigation.NavBar;

@Component
@EnableConfigurationProperties(BreuningerApplicationProperties.class)
public class NavigationConfiguration {

  @Autowired
  public NavigationConfiguration(final NavBar mainNavBar, final BreuningerApplicationProperties breuningerApplicationProperties) {
    mainNavBar.register(navBarItem(0, "Home", "/"));
    mainNavBar.register(navBarItem(1, "Feature Toggles",
      format("%s/toggles/console/index", breuningerApplicationProperties.getManagement().getBasePath())));
  }
}
