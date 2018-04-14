package com.breuninger.boot.status.indicator;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import static com.breuninger.boot.status.domain.Status.ERROR;
import static com.breuninger.boot.status.domain.Status.OK;
import static com.breuninger.boot.status.domain.StatusDetail.statusDetail;

import org.junit.Test;

public class CompositeStatusDetailIndicatorTest {

  @Test(expected = IllegalArgumentException.class)
  public void shouldNotAcceptEmptyListOfDelegates() {
    new CompositeStatusDetailIndicator("foo", emptyList());
  }

  @Test
  public void shouldReturnStatusDetailOfSingleIndicator() {
    final StatusDetailIndicator indicator = new MutableStatusDetailIndicator(statusDetail("bar", ERROR, "a message"));
    final var composite = new CompositeStatusDetailIndicator("foo", singletonList(indicator));
    assertThat(composite.statusDetail(), is(statusDetail("bar", ERROR, "a message")));
  }

  @Test
  public void shouldAggregateStatusDetails() {
    final StatusDetailIndicator first = new MutableStatusDetailIndicator(statusDetail("firstIndicator", OK, "a message"));
    final StatusDetailIndicator second = new MutableStatusDetailIndicator(statusDetail("secondIndicator", ERROR, "a message"));
    final var composite = new CompositeStatusDetailIndicator("foo", asList(first, second));
    assertThat(composite.statusDetail(), is(statusDetail("foo", ERROR, "Aggregated status of 2 delegate indicators")));
  }

  @Test
  public void shouldProvideStatusDetails() {
    final StatusDetailIndicator first = new MutableStatusDetailIndicator(statusDetail("firstIndicator", OK, "a message"));
    final StatusDetailIndicator second = new MutableStatusDetailIndicator(statusDetail("secondIndicator", ERROR, "a message"));
    final var composite = new CompositeStatusDetailIndicator("foo", asList(first, second));
    assertThat(composite.statusDetails(),
      contains(statusDetail("firstIndicator", OK, "a message"), statusDetail("secondIndicator", ERROR, "a message")));
  }
}
