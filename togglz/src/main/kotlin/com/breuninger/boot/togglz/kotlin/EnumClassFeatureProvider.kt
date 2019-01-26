package com.breuninger.boot.togglz.kotlin

import com.breuninger.boot.togglz.kotlin.domain.FeatureEnum
import com.breuninger.boot.togglz.kotlin.domain.FeatureEnumMetaData
import org.togglz.core.Feature
import org.togglz.core.metadata.FeatureMetaData
import org.togglz.core.spi.FeatureProvider
import java.util.Collections.unmodifiableSet

class EnumClassFeatureProvider(featureClass: Class<out Enum<*>>) : FeatureProvider {

  private val features = featureClass.enumConstants.map {
    it to FeatureEnum(it)
  }.toMap()
  private val metaData = featureClass.enumConstants.map {
    it.name to FeatureEnumMetaData(it, FeatureEnum(it))
  }.toMap()

  override fun getFeatures(): Set<Feature> {
    return unmodifiableSet(HashSet(features.values))
  }

  override fun getMetaData(feature: Feature): FeatureMetaData? {
    return metaData[feature.name()]
  }
}
