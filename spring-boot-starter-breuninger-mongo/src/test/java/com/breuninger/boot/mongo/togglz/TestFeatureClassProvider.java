package com.breuninger.boot.mongo.togglz;

import org.togglz.core.Feature;

import com.breuninger.boot.togglz.FeatureClassProvider;

class TestFeatureClassProvider implements FeatureClassProvider {
  @Override
  public Class<? extends Feature> getFeatureClass() {
    return TestFeatures.class;
  }
}
