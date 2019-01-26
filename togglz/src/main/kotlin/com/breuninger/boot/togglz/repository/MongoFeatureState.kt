package com.breuninger.boot.togglz.repository

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.togglz.core.Feature
import org.togglz.core.repository.FeatureState

@Document(collection = "togglz")
data class MongoFeatureState(@Id val feature: String,
                             val enabled: Boolean,
                             val strategyId: String?,
                             val parameters: Map<String, String>) {

  fun toFeatureState(feature: Feature): FeatureState {
    val featureState = FeatureState(feature)
    featureState.isEnabled = enabled
    featureState.strategyId = strategyId
    parameters.forEach {
      featureState.setParameter(it.key, it.value)
    }
    return featureState
  }
}
