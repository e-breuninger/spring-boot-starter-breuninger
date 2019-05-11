package com.breuninger.boot.togglz.actuator

import assertk.assertThat
import assertk.assertions.containsAll
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.breuninger.boot.togglz.domain.TogglzFeature
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.togglz.core.Feature
import org.togglz.core.manager.FeatureManager
import org.togglz.core.repository.FeatureState

class TogglzEndpointTest {

  private val featureManager = mockk<FeatureManager>()
  private val togglzEndpoint = TogglzEndpoint(featureManager)
  private val featureA = Feature { "a" }
  private val featureB = Feature { "b" }
  private val featureC = Feature { "c" }
  private val featureStateA = FeatureState { "a" }
  private val featureStateB = FeatureState { "b" }
  private val featureStateC = FeatureState { "c" }

  @BeforeEach
  fun beforeEach() {
    every { featureManager.features } returns setOf(featureA, featureB, featureC)
    every { featureManager.getFeatureState(featureA) } returns featureStateA
    every { featureManager.getFeatureState(featureB) } returns featureStateB
    every { featureManager.getFeatureState(featureC) } returns featureStateC
  }

  @Test
  fun `ensure that getAllFeatures returns all features`() {
    val features = togglzEndpoint.getAllFeatures()

    assertThat(features.size).isEqualTo(3)
    assertThat(features).containsAll(
      TogglzFeature(featureA, featureStateA), TogglzFeature(featureB, featureStateB), TogglzFeature(featureC, featureStateC))
  }

  @Test
  fun `ensure enabling and disabeling of features works`() {
    every { featureManager.setFeatureState(any()) } returns Unit

    assertThat(featureStateA.isEnabled).isFalse()
    togglzEndpoint.setFeatureState("a", true)

    assertThat(featureStateA.isEnabled).isTrue()
    togglzEndpoint.setFeatureState("a", false)

    assertThat(featureStateA.isEnabled).isFalse()
    togglzEndpoint.setFeatureState("a", false)

    assertThat(featureStateA.isEnabled).isFalse()
  }
}
