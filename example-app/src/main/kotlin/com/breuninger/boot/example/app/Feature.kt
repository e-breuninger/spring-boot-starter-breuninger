package com.breuninger.boot.example.app

import com.breuninger.boot.togglz.kotlin.EnumClassFeatureProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.togglz.core.context.FeatureContext
import org.togglz.core.spi.FeatureProvider

enum class Feature {
  REST_PRODUCTS_FINDALL;

  fun isActive() = FeatureContext.getFeatureManager().isActive { name }
}

@Configuration
class FeatureProviderConfiguration {

  @Bean
  fun featureProvider(): FeatureProvider {
    return EnumClassFeatureProvider(Feature::class.java)
  }
}
