package com.breuninger.boot.testsupport.togglz;

import org.togglz.core.manager.FeatureManager;
import org.togglz.core.spi.FeatureManagerProvider;

public class TestFeatureManagerProvider implements FeatureManagerProvider {

  private static FeatureManager instance;

  @Override
  public int priority() {
    return 10;
  }

  @Override
  public FeatureManager getFeatureManager() {
    return instance;
  }

  public static void setFeatureManager(final FeatureManager instance) {
    TestFeatureManagerProvider.instance = instance;
  }
}

