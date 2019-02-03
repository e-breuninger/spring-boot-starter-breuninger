package com.breuninger.boot.togglz.autoconfigure

import com.breuninger.boot.togglz.actuator.TogglzEndpoint
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint
import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.togglz.core.manager.FeatureManager

@Configuration
@ConditionalOnClass(Endpoint::class)
@AutoConfigureAfter(TogglzAutoConfiguration::class)
class TogglzEndpointAutoConfiguration {

  @Bean
  @ConditionalOnEnabledEndpoint
  fun togglzEndpoint(featureManager: FeatureManager): TogglzEndpoint {
    return TogglzEndpoint(featureManager)
  }
}
