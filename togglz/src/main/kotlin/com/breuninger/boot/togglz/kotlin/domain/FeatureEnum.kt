package com.breuninger.boot.togglz.kotlin.domain

import org.togglz.core.Feature

data class FeatureEnum(private val featureEnum: Enum<*>) : Feature {

  override fun name(): String {
    return featureEnum.name
  }
}
