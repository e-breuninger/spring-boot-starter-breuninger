package com.breuninger.boot.togglz.repository.cleanup

import com.breuninger.boot.togglz.domain.TogglzFeature
import io.micrometer.core.annotation.Timed
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.*
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.remove
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.togglz.core.manager.FeatureManager

@Component
class DeleteToggleStatesWithoutTogglz(
  private val featureManager: FeatureManager,
  private val togglzMongoTemplate: MongoTemplate
) {

  companion object {

    private const val DELETE_TOGGLE_STATES_WITHOUT_TOGGLZ_CLEANUP_FIXED_RATE = 1L * 60L * 1000L
  }

  @Timed("com.breuninger.boot.togglz.repository.cleanup.DeleteToggleStatesWithoutTogglz.cleanUp", longTask = true)
  @Scheduled(initialDelay = 0, fixedRate = DELETE_TOGGLE_STATES_WITHOUT_TOGGLZ_CLEANUP_FIXED_RATE)
  fun cleanUp() {
    val knownToggleIds = featureManager.features.map { it.name() }
    togglzMongoTemplate.remove<TogglzFeature>(query(where("_id").not().`in`(knownToggleIds)))
  }
}
