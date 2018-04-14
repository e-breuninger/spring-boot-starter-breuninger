package com.breuninger.boot.example.configuration;

import static com.breuninger.boot.navigation.NavBarItem.navBarItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.breuninger.boot.navigation.NavBar;

@Component
@EnableConfigurationProperties(WebEndpointProperties.class)
public class NavigationConfiguration {

  @Autowired
  public NavigationConfiguration(final NavBar mainNavBar, final WebEndpointProperties webEndpointProperties) {
    mainNavBar.register(navBarItem(0, "Home", "/"));
    mainNavBar.register(navBarItem(2, "Metrics", webEndpointProperties.getBasePath() + "/metrics"));
  }
}
