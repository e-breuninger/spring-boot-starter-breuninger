package com.breuninger.boot.status.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.breuninger.boot.annotations.Beta;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "breuninger.status.cluster")
@Beta
public class ClusterInfoProperties {

  private boolean enabled;
  private String colorHeader = "X-Color";
  private String colorStateHeader = "X-Staging";
}
