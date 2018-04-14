package com.breuninger.boot.authentication.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.breuninger.boot.authentication.LdapAuthenticationFilter;
import com.breuninger.boot.authentication.connection.LdapConnectionFactory;
import com.breuninger.boot.authentication.connection.SSLLdapConnectionFactory;
import com.breuninger.boot.authentication.connection.StartTlsLdapConnectionFactory;

@Configuration
@EnableConfigurationProperties(LdapProperties.class)
@ConditionalOnProperty(prefix = "breuninger.ldap", name = "enabled", havingValue = "true")
@ConditionalOnMissingBean(name = "ldapAuthenticationFilter")
public class LdapConfiguration {

  @Bean
  @ConditionalOnMissingBean(LdapConnectionFactory.class)
  public LdapConnectionFactory ldapConnectionFactory(final LdapProperties ldapProperties) {
    if (ldapProperties.getEncryptionType() == EncryptionType.SSL) {
      return new SSLLdapConnectionFactory(ldapProperties);
    }
    return new StartTlsLdapConnectionFactory(ldapProperties);
  }

  @Bean
  public FilterRegistrationBean<LdapAuthenticationFilter> ldapAuthenticationFilter(final LdapProperties ldapProperties,
                                                                                   final LdapConnectionFactory ldapConnectionFactory) {
    final FilterRegistrationBean<LdapAuthenticationFilter> filterRegistration = new FilterRegistrationBean<>();
    filterRegistration.setFilter(new LdapAuthenticationFilter(ldapProperties, ldapConnectionFactory));
    filterRegistration.addUrlPatterns(String.format("%s/*", ldapProperties.getPrefix()));
    return filterRegistration;
  }
}
