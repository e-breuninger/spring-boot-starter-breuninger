package com.breuninger.boot.togglz.cache

import com.breuninger.boot.togglz.domain.TogglzFeature
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.togglz.core.manager.FeatureManager

@Component
class TogglzCacheWarmer(private val featureManager: FeatureManager) {

  companion object {

    private const val WARM_TOGGLZ_CACHE_FIXED_RATE = 1L * 60L * 1000L
  }

  @Scheduled(initialDelay = 0, fixedRate = WARM_TOGGLZ_CACHE_FIXED_RATE)
  fun warmTogglzCache() {
    featureManager.features.map { TogglzFeature(it, featureManager.getFeatureState(it)) }
  }
}
