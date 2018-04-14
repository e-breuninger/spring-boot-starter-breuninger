package com.breuninger.boot.logging.ui;

import static java.util.Collections.singletonMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class DisableEndpointPostProcessor implements BeanFactoryPostProcessor {

  private final String endpoint;

  DisableEndpointPostProcessor(final String endpoint) {
    this.endpoint = endpoint;
  }

  @Override
  public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
    LOG.info("Disabling '{}' Endpoint", endpoint);
    disableEndpoint(beanFactory);
  }

  private void disableEndpoint(final ConfigurableListableBeanFactory beanFactory) {
    final var env = beanFactory.getBean(ConfigurableEnvironment.class);
    final var propertySources = env.getPropertySources();
    propertySources.addFirst(
      new MapPropertySource(endpoint + "PropertySource", singletonMap("endpoints." + endpoint + ".enabled", false)));
  }
}
