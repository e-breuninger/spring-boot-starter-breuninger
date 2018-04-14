package com.breuninger.boot.testsupport.togglz;

import static org.togglz.core.context.FeatureContext.clearCache;
import static org.togglz.core.context.FeatureContext.getFeatureManager;

import org.togglz.core.Feature;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.util.FeatureAnnotations;

public class FeatureManagerSupport {

  public static void allEnabledFeatureConfig(final Class<? extends Feature> featureClass) {
    final var featureManager = new TestFeatureManager(featureClass);
    enableAllFeaturesThatAreOkToEnableByDefaultInAllTests(featureClass, featureManager);
    TestFeatureManagerProvider.setFeatureManager(featureManager);
    clearCache();
  }

  public static void allDisabledFeatureConfig(final Class<? extends Feature> featureClass) {
    final var featureManager = new TestFeatureManager(featureClass);
    for (final Feature feature : featureClass.getEnumConstants()) {
      featureManager.disable(feature);
    }
    TestFeatureManagerProvider.setFeatureManager(featureManager);
    clearCache();
  }

  public static void disable(final Feature feature) {
    getFeatureManager().setFeatureState(new FeatureState(feature, false));
  }

  private static void enableAllFeaturesThatAreOkToEnableByDefaultInAllTests(final Class<? extends Feature> featureClass,
                                                                            final TestFeatureManager featureManager) {
    for (final Feature feature : featureClass.getEnumConstants()) {
      if (shouldRunInTests(feature)) {
        featureManager.enable(feature);
      }
    }
  }

  public static boolean shouldRunInTests(final Feature feature) {
    final var label = FeatureAnnotations.getLabel(feature);
    return !label.contains("[inactiveInTests]");
  }

  public static void enable(final Feature feature) {
    getFeatureManager().setFeatureState(new FeatureState(feature, true));
  }
}
