package com.breuninger.boot.togglz.repository

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository
import org.togglz.core.Feature
import org.togglz.core.repository.FeatureState
import org.togglz.core.repository.StateRepository

@Repository
@ConditionalOnProperty(prefix = "togglz", name = ["mongo.enabled"], havingValue = "true")
class MongoStateRepositoryImpl(val mongoTemplate: MongoTemplate) : StateRepository {

  override fun getFeatureState(feature: Feature): FeatureState? {
    return mongoTemplate.findById(feature.name(), MongoFeatureState::class.java)?.toFeatureState(feature)
  }

  override fun setFeatureState(featureState: FeatureState) {
    mongoTemplate.save(
      MongoFeatureState(featureState.feature.name(), featureState.isEnabled, featureState.strategyId, featureState.parameterMap))
  }
}
