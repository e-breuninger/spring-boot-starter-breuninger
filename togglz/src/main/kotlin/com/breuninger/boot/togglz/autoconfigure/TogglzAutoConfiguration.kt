package com.breuninger.boot.togglz.autoconfigure

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.togglz.core.context.StaticFeatureManagerProvider
import org.togglz.core.manager.FeatureManager
import org.togglz.core.manager.FeatureManagerBuilder
import org.togglz.core.repository.StateRepository
import org.togglz.core.repository.cache.CachingStateRepository
import org.togglz.core.repository.mem.InMemoryStateRepository
import org.togglz.core.spi.FeatureProvider

@Configuration
@EnableConfigurationProperties(TogglzProperties::class)
class TogglzAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  fun stateRepository(): StateRepository {
    return InMemoryStateRepository()
  }

  @Bean
  fun featureManager(togglzProperties: TogglzProperties, featureProvider: FeatureProvider,
                     stateRepository: StateRepository): FeatureManager {
    val featureManager = FeatureManagerBuilder().featureProvider(featureProvider)
      .stateRepository(
        CachingStateRepository(stateRepository, togglzProperties.cache.timeToLive, togglzProperties.cache.timeUnit)).build()
    StaticFeatureManagerProvider.setFeatureManager(featureManager)
    return featureManager
  }
}
