package com.breuninger.boot.togglz;

import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum TestFeatures implements Feature {

  @Label("a test feature toggle") TEST_FEATURE, TEST_FEATURE_2;

  public boolean isActive() {
    return FeatureContext.getFeatureManager().isActive(this);
  }
}
