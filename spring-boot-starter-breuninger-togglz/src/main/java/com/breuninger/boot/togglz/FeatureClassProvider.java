package com.breuninger.boot.togglz;

import org.togglz.core.Feature;

public interface FeatureClassProvider {

  Class<? extends Feature> getFeatureClass();
}
