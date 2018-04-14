package com.breuninger.boot.testsupport.util;

import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Set;

public final class Sets {

  @SafeVarargs
  public static <T> Set<T> hashSet(final T... values) {
    final Set<T> result = new HashSet<>();
    if (values == null) {
      return result;
    }
    result.addAll(asList(values));
    return result;
  }
}
