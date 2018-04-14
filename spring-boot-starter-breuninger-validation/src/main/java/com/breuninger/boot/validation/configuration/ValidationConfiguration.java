package com.breuninger.boot.validation.configuration;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class ValidationConfiguration {

  @Bean
  public ResourceBundleMessageSource breuningerValidationMessageSource() {
    final var source = new ResourceBundleMessageSource();
    source.setBasename("ValidationMessages");
    source.setUseCodeAsDefaultMessage(true);
    return source;
  }

  @Bean
  public LocalValidatorFactoryBean validator() {
    final var resourceBundleLocator = new PlatformResourceBundleLocator(
      ResourceBundleMessageInterpolator.USER_VALIDATION_MESSAGES, null, true);

    final var factoryBean = new LocalValidatorFactoryBean();
    factoryBean.setMessageInterpolator(new ResourceBundleMessageInterpolator(resourceBundleLocator));
    return factoryBean;
  }
}
