package com.breuninger.boot.togglz.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test
import org.togglz.core.Feature
import org.togglz.core.repository.FeatureState


internal class TogglzFeatureTest {

  @Test
  fun `ensure toFeatureState copies the current FeatureState correctly`() {
    val featureState = createFeatureState(Feature { "a" })
    val togglzFeature = TogglzFeature(Feature { "a" }, featureState)
    val newFeatureState = togglzFeature.toFeatureState(Feature { "b" })
    assertThat(newFeatureState.isEnabled).isTrue()
    assertThat(newFeatureState.strategyId).isEqualTo("foo bar bazz")
    assertThat(newFeatureState.getParameter("a")).isEqualTo("b")
    assertThat(newFeatureState.getParameter("c")).isEqualTo("d")
  }

  @Test
  fun `ensure that two TogglzFeatures are equal if the feature names equal`() {
    val togglzFeature = TogglzFeature("a", false, "b", emptyMap())
    val otherTogglzFeature = TogglzFeature("a", true, "c", mapOf("d" to "e"))

    assertThat(togglzFeature.compareTo(otherTogglzFeature)).isEqualTo(0)
  }

  @Test
  fun `ensure that two TogglzFeatures are not equal if the feature names are not equal`() {
    val togglzFeature = TogglzFeature("a", false, "b", emptyMap())
    val otherTogglzFeature = TogglzFeature("b", false, "b", emptyMap())

    assertThat(togglzFeature.compareTo(otherTogglzFeature)).isNotEqualTo(0)
  }

  private fun createFeatureState(feature: Feature): FeatureState {
    val featureState = FeatureState(feature)
    featureState.isEnabled = true
    featureState.strategyId = "foo bar bazz"
    featureState.setParameter("a", "b")
    featureState.setParameter("c", "d")
    return featureState
  }
}
