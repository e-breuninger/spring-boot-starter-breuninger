package com.breuninger.boot.togglz.kotlin.util

import org.togglz.core.annotation.EnabledByDefault
import org.togglz.core.annotation.Label

object EnumAnnotations {

  fun getLabel(featureEnum: Enum<*>): String {
    return getAnnotation(featureEnum, Label::class.java)?.value ?: featureEnum.name
  }

  fun isEnabledByDefault(featureEnum: Enum<*>): Boolean {
    return getAnnotation(featureEnum, EnabledByDefault::class.java) != null
  }

  fun <A : Annotation> getAnnotation(featureEnum: Enum<*>, annotationClass: Class<A>): A? {
    return featureEnum.javaClass.getField(featureEnum.name).getAnnotation(annotationClass)
      ?: featureEnum.javaClass.getAnnotation(annotationClass)
  }

  fun getAnnotations(featureEnum: Enum<*>): Set<Annotation> {
    return featureEnum::name.annotations.union(featureEnum::class.annotations)
  }
}
