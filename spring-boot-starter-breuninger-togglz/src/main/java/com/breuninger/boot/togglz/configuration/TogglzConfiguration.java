package com.breuninger.boot.togglz.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.togglz.core.Feature;
import org.togglz.core.context.StaticFeatureManagerProvider;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.manager.TogglzConfig;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.user.SimpleFeatureUser;
import org.togglz.core.user.UserProvider;
import org.togglz.servlet.TogglzFilter;
import org.togglz.servlet.util.HttpServletRequestHolder;
import org.togglz.spring.manager.FeatureManagerFactory;

import com.breuninger.boot.authentication.Credentials;
import com.breuninger.boot.togglz.DefaultTogglzConfig;
import com.breuninger.boot.togglz.FeatureClassProvider;

@Configuration
@EnableConfigurationProperties(TogglzProperties.class)
public class TogglzConfiguration {

  @Bean
  @ConditionalOnMissingBean(name = "togglzFilter")
  public FilterRegistrationBean<TogglzFilter> togglzFilter() {
    final FilterRegistrationBean<TogglzFilter> filterRegistration = new FilterRegistrationBean<>();
    filterRegistration.setFilter(new TogglzFilter());
    filterRegistration.addUrlPatterns("/*");
    return filterRegistration;
  }

  @Bean
  @ConditionalOnMissingBean(FeatureClassProvider.class)
  public FeatureClassProvider featureClassProvider() {
    return () -> Features.class;
  }

  @Bean
  @ConditionalOnMissingBean(UserProvider.class)
  public UserProvider userProvider() {
    return () -> {

      final var request = HttpServletRequestHolder.get();

      final var credentials = Credentials.readFrom(request);
      final var isAdmin = true;

      return new SimpleFeatureUser(credentials.map(Credentials::getUsername).orElse(null), isAdmin);
    };
  }

  @Bean
  public TogglzConfig togglzConfig(final StateRepository stateRepository, final FeatureClassProvider featureClassProvider,
                                   final TogglzProperties togglzProperties) {
    return new DefaultTogglzConfig(togglzProperties, stateRepository, userProvider(), featureClassProvider);
  }

  @Bean
  public FeatureManager featureManager(final TogglzConfig togglzConfig) throws Exception {
    final var featureManagerFactory = new FeatureManagerFactory();
    featureManagerFactory.setTogglzConfig(togglzConfig);
    final var featureManager = featureManagerFactory.getObject();
    StaticFeatureManagerProvider.setFeatureManager(featureManager);
    return featureManager;
  }

  private enum Features implements Feature {
    /* no features */
  }
}
