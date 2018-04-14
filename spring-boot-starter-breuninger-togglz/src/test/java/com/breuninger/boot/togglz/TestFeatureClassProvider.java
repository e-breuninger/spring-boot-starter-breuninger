package com.breuninger.boot.togglz;

import org.springframework.stereotype.Component;
import org.togglz.core.Feature;

@Component
class TestFeatureClassProvider implements FeatureClassProvider {

  @Override
  public Class<? extends Feature> getFeatureClass() {
    return TestFeatures.class;
  }
}
