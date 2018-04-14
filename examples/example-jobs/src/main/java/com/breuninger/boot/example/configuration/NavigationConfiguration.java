package com.breuninger.boot.example.configuration;

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
    mainNavBar.register(
      navBarItem(1, "Status", String.format("%s/status", breuningerApplicationProperties.getManagement().getBasePath())));
    mainNavBar.register(
      navBarItem(2, "Job Overview", String.format("%s/jobs", breuningerApplicationProperties.getManagement().getBasePath())));
    mainNavBar.register(navBarItem(3, "Job Definitions",
      String.format("%s/jobdefinitions", breuningerApplicationProperties.getManagement().getBasePath())));
  }
}
