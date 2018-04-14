package com.breuninger.boot.example.togglz;

import org.springframework.stereotype.Component;
import org.togglz.core.Feature;

@Component
public class FeatureClassProvider implements com.breuninger.boot.togglz.FeatureClassProvider {

  @Override
  public Class<? extends Feature> getFeatureClass() {
    return Features.class;
  }
}
