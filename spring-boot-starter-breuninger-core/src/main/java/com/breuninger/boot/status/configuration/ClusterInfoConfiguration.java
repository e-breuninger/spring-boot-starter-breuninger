package com.breuninger.boot.status.configuration;

import static org.springframework.web.context.request.RequestContextHolder.getRequestAttributes;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.breuninger.boot.annotations.Beta;
import com.breuninger.boot.status.domain.ClusterInfo;

@Configuration
@EnableConfigurationProperties(ClusterInfoProperties.class)
@Beta
public class ClusterInfoConfiguration {

  private final ClusterInfoProperties clusterInfoProperties;

  public ClusterInfoConfiguration(final ClusterInfoProperties clusterInfoProperties) {
    this.clusterInfoProperties = clusterInfoProperties;
  }

  private static String httpHeaderValue(final String header) {
    final String value;
    final var requestAttributes = (ServletRequestAttributes) getRequestAttributes();
    value = requestAttributes.getRequest().getHeader(header);
    return value != null ? value : "";
  }

  @Bean
  @ConditionalOnMissingBean(ClusterInfo.class)
  public ClusterInfo clusterInfo() {
    return ClusterInfo.clusterInfo(() -> httpHeaderValue(clusterInfoProperties.getColorHeader()),
      () -> httpHeaderValue(clusterInfoProperties.getColorStateHeader()));
  }
}
