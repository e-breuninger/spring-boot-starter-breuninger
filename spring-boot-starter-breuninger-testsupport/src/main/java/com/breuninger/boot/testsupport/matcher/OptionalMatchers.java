package com.breuninger.boot.testsupport.matcher;

import java.util.Optional;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class OptionalMatchers {

  public static Matcher<? super Optional<?>> isPresent() {
    return new BaseMatcher() {

      @Override
      public boolean matches(final Object item) {
        return Optional.class.isAssignableFrom(item.getClass()) && ((Optional) item).isPresent();
      }

      @Override
      public void describeTo(final Description description) {
        description.appendText("Optional should be present");
      }
    };
  }

  public static Matcher<? super Optional<?>> isAbsent() {
    return new BaseMatcher() {

      @Override
      public boolean matches(final Object item) {
        return Optional.class.isAssignableFrom(item.getClass()) && !((Optional) item).isPresent();
      }

      @Override
      public void describeTo(final Description description) {
        description.appendText("Optional should be absent");
      }
    };
  }
}
