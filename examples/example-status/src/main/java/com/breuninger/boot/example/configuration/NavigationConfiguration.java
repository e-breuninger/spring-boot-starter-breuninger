package com.breuninger.boot.example.configuration;

import static com.breuninger.boot.navigation.NavBarItem.navBarItem;
import static com.breuninger.boot.navigation.NavBarItem.top;

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
    mainNavBar.register(
      navBarItem(top(), "Status", String.format("%s/status", breuningerApplicationProperties.getManagement().getBasePath())));
  }
}
