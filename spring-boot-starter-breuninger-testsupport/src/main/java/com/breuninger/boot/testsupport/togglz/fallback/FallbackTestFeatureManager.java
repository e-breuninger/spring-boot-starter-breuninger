package com.breuninger.boot.testsupport.togglz.fallback;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.togglz.core.Feature;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.metadata.EmptyFeatureMetaData;
import org.togglz.core.metadata.FeatureMetaData;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.spi.ActivationStrategy;
import org.togglz.core.user.FeatureUser;
import org.togglz.core.user.SimpleFeatureUser;

import com.breuninger.boot.testsupport.togglz.FeatureManagerSupport;

public class FallbackTestFeatureManager implements FeatureManager {

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public Set<Feature> getFeatures() {
    return Collections.emptySet();
  }

  @Override
  public FeatureMetaData getMetaData(final Feature feature) {
    return new EmptyFeatureMetaData(feature);
  }

  @Override
  public boolean isActive(final Feature feature) {

    return FeatureManagerSupport.shouldRunInTests(feature);
  }

  @Override
  public FeatureUser getCurrentFeatureUser() {
    final var featureAdmin = true;
    return new SimpleFeatureUser("p13n-testing-user", featureAdmin);
  }

  @Override
  public FeatureState getFeatureState(final Feature feature) {
    return new FeatureState(feature, true);
  }

  @Override
  public void setFeatureState(final FeatureState state) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<ActivationStrategy> getActivationStrategies() {
    return Collections.emptyList();
  }
}
