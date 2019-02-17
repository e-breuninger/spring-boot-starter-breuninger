package com.breuninger.boot.togglz.repository

import com.breuninger.boot.togglz.domain.TogglzFeature
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository
import org.togglz.core.Feature
import org.togglz.core.repository.FeatureState
import org.togglz.core.repository.StateRepository

@Repository
@ConditionalOnProperty(prefix = "breuni.togglz", name = ["mongo.enabled"], havingValue = "true")
class MongoStateRepository(private val mongoTemplate: MongoTemplate) : StateRepository {

  override fun getFeatureState(feature: Feature) = mongoTemplate.findById(feature.name(),
    TogglzFeature::class.java)?.toFeatureState(feature)

  override fun setFeatureState(featureState: FeatureState) {
    mongoTemplate.save(TogglzFeature(featureState.feature, featureState))
  }
}
