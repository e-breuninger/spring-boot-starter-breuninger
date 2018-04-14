package com.breuninger.boot.togglz.controller;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import static org.togglz.core.context.FeatureContext.getFeatureManager;

import java.util.Map;

import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;

import com.breuninger.boot.togglz.FeatureClassProvider;

import net.jcip.annotations.Immutable;

@Immutable
public class FeatureTogglesRepresentation {

  public final Map<String, FeatureToggleRepresentation> features;

  private FeatureTogglesRepresentation(final Class<? extends Feature> featureClass) {
    features = buildTogglzState(featureClass);
  }

  public static FeatureTogglesRepresentation togglzRepresentation(final FeatureClassProvider featureClassProvider) {
    return new FeatureTogglesRepresentation(featureClassProvider.getFeatureClass());
  }

  public static Label getLabelAnnotation(final Feature feature) {
    try {
      final Class<? extends Feature> featureClass = feature.getClass();
      final var fieldAnnotation = featureClass.getField(feature.name()).getAnnotation(Label.class);
      final var classAnnotation = featureClass.getAnnotation(Label.class);

      return fieldAnnotation != null ? fieldAnnotation : classAnnotation;
    } catch (final SecurityException | NoSuchFieldException e) {
    }
    return null;
  }

  private Map<String, FeatureToggleRepresentation> buildTogglzState(final Class<? extends Feature> featureClass) {
    final Feature[] features = featureClass.getEnumConstants();
    return stream(features).collect(toMap(Feature::name, this::toFeatureToggleRepresentation));
  }

  private FeatureToggleRepresentation toFeatureToggleRepresentation(final Feature feature) {
    final var label = getLabelAnnotation(feature);
    return new FeatureToggleRepresentation(label != null ? label.value() : feature.name(),
      getFeatureManager().getFeatureState(feature).isEnabled(), null);
  }
}
