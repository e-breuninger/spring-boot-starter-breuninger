package com.breuninger.boot.togglz.configuration;

import static com.breuninger.boot.navigation.NavBarItem.bottom;
import static com.breuninger.boot.navigation.NavBarItem.navBarItem;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.togglz.console.TogglzConsoleServlet;

import com.breuninger.boot.navigation.NavBar;

@Configuration
@EnableConfigurationProperties(TogglzProperties.class)
@ConditionalOnProperty(prefix = "breuninger.togglz.console", name = "enabled", havingValue = "true", matchIfMissing = true)
public class TogglzConsoleConfiguration {

  public static final String TOGGLES_URL_PATTERN = "/toggles/console/*";

  @Bean
  public ServletRegistrationBean<?> togglzServlet(
    @Value("${breuninger.application.management.base-path:/internal}") final String prefix, final NavBar rightNavBar) {

    rightNavBar.register(navBarItem(bottom(), "Feature Toggles", prefix + "/toggles/console"));
    return new ServletRegistrationBean<>(new TogglzConsoleServlet(), prefix + TOGGLES_URL_PATTERN);
  }
}
