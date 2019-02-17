package com.breuninger.boot.togglz.actuator

import com.breuninger.boot.togglz.domain.TogglzFeature
import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation
import org.springframework.boot.actuate.endpoint.annotation.Selector
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation
import org.togglz.core.manager.FeatureManager

@Endpoint(id = "togglz")
class TogglzEndpoint(private val featureManager: FeatureManager) {

  @ReadOperation
  fun getAllFeatures() = featureManager.features.map {
    val featureState = featureManager.getFeatureState(it)
    TogglzFeature(it, featureState)
  }.sorted()

  @WriteOperation
  @Throws(IllegalArgumentException::class)
  fun setFeatureState(@Selector featureName: String, enabled: Boolean): TogglzFeature {
    val feature = featureManager.features.find { it.name() == featureName }
      ?: throw IllegalArgumentException("Could not find feature with name $featureName")

    val featureState = featureManager.getFeatureState(feature)
    featureState.isEnabled = enabled
    featureManager.setFeatureState(featureState)

    return TogglzFeature(feature, featureState)
  }
}
