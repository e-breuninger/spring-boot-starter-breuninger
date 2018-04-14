package com.breuninger.boot.navigation;

import static java.util.Collections.singletonList;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.breuninger.boot.configuration.BreuningerApplicationProperties;

@Configuration
@EnableConfigurationProperties(BreuningerApplicationProperties.class)
public class NavBarConfiguration {

  @Bean
  public NavBar mainNavBar() {
    return NavBar.emptyNavBar();
  }

  @Bean
  public NavBar rightNavBar(final BreuningerApplicationProperties properties) {
    final var href = properties.getManagement().getBasePath() + "/status";
    return NavBar.navBar(singletonList(NavBarItem.navBarItem(NavBarItem.top(), "Status", href)));
  }
}
