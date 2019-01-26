package com.breuninger.boot.togglz.kotlin.domain

import com.breuninger.boot.togglz.kotlin.util.EnumAnnotations
import org.togglz.core.Feature
import org.togglz.core.annotation.DefaultActivationStrategy
import org.togglz.core.metadata.FeatureGroup
import org.togglz.core.metadata.FeatureMetaData
import org.togglz.core.metadata.enums.AnnotationFeatureGroup
import org.togglz.core.repository.FeatureState
import org.togglz.core.util.FeatureAnnotations
import java.util.Collections.unmodifiableMap

class FeatureEnumMetaData(featureEnum: Enum<*>, feature: Feature) : FeatureMetaData {

  private val label = EnumAnnotations.getLabel(featureEnum)
  private val defaultFeatureState = FeatureState(feature, EnumAnnotations.isEnabledByDefault(featureEnum))
  private val groups = EnumAnnotations.getAnnotations(featureEnum).map { AnnotationFeatureGroup.build(it.annotationClass.java) }
    .toSet()
  private val attributes = EnumAnnotations.getAnnotations(featureEnum).map { FeatureAnnotations.getFeatureAttribute(it) }
    .map { it[0] to it[1] }.toMap()

  init {
    EnumAnnotations.getAnnotation(featureEnum, DefaultActivationStrategy::class.java)?.let {
      defaultFeatureState.strategyId = it.id
      for (parameter in it.parameters) {
        defaultFeatureState.setParameter(parameter.name, parameter.value)
      }
    }
  }

  override fun getLabel(): String {
    return label
  }

  override fun getDefaultFeatureState(): FeatureState {
    return defaultFeatureState.copy()
  }

  override fun getGroups(): Set<FeatureGroup> {
    return groups
  }

  override fun getAttributes(): Map<String, String> {
    return unmodifiableMap(attributes)
  }
}
