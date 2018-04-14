package com.breuninger.boot.testsupport.togglz;

import static java.util.Collections.emptyList;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.togglz.core.Feature;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.metadata.FeatureMetaData;
import org.togglz.core.metadata.enums.EnumFeatureMetaData;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.spi.ActivationStrategy;
import org.togglz.core.user.FeatureUser;
import org.togglz.core.user.SimpleFeatureUser;
import org.togglz.core.util.Validate;

public class TestFeatureManager implements FeatureManager {

  private final Class<? extends Feature> featureEnum;

  private final Set<String> activeFeatures = new HashSet<>();

  public TestFeatureManager(final Class<? extends Feature> featureEnum) {
    Validate.notNull(featureEnum, "The featureEnum argument is required");
    Validate.isTrue(featureEnum.isEnum(), "This feature manager currently only works with feature enums");
    this.featureEnum = featureEnum;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName() + ":" + featureEnum.getSimpleName();
  }

  @Override
  public Set<Feature> getFeatures() {
    return new HashSet<>(Arrays.asList(featureEnum.getEnumConstants()));
  }

  @Override
  public FeatureMetaData getMetaData(final Feature feature) {
    return new EnumFeatureMetaData(feature);
  }

  @Override
  public boolean isActive(final Feature feature) {
    return activeFeatures.contains(feature.name());
  }

  @Override
  public FeatureUser getCurrentFeatureUser() {
    final var featureAdmin = true;
    return new SimpleFeatureUser("p13n-testing-user", featureAdmin);
  }

  @Override
  public FeatureState getFeatureState(final Feature feature) {
    return new FeatureState(feature, isActive(feature));
  }

  @Override
  public void setFeatureState(final FeatureState state) {
    if (state.isEnabled()) {
      activeFeatures.add(state.getFeature().name());
    } else {
      activeFeatures.remove(state.getFeature().name());
    }
  }

  @Override
  public List<ActivationStrategy> getActivationStrategies() {
    return emptyList();
  }

  public TestFeatureManager enable(final Feature feature) {
    activeFeatures.add(feature.name());
    return this;
  }

  public TestFeatureManager disable(final Feature feature) {
    activeFeatures.remove(feature.name());
    return this;
  }

  public TestFeatureManager enableAll() {
    for (final Feature feature : featureEnum.getEnumConstants()) {
      enable(feature);
    }
    return this;
  }

  public TestFeatureManager disableAll() {
    activeFeatures.clear();
    return this;
  }
}
