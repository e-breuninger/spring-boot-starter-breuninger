package com.breuninger.boot.togglz.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.togglz.core.Feature
import org.togglz.core.repository.FeatureState

@Document(collection = "togglz")
data class TogglzFeature(@Id val feature: String,
                         val enabled: Boolean,
                         val strategyId: String?,
                         val parameters: Map<String, String>)
  : Comparable<TogglzFeature> {

  constructor(feature: Feature, featureState: FeatureState)
    : this(feature.name(), featureState.isEnabled, featureState.strategyId, featureState.parameterMap)

  fun toFeatureState(feature: Feature): FeatureState {
    val featureState = FeatureState(feature)
    featureState.isEnabled = enabled
    featureState.strategyId = strategyId
    parameters.forEach {
      featureState.setParameter(it.key, it.value)
    }
    return featureState
  }

  override fun compareTo(other: TogglzFeature): Int {
    return feature.compareTo(other.feature);
  }
}
