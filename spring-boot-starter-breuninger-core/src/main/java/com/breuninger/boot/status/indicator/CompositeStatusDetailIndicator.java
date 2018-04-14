package com.breuninger.boot.status.indicator;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;

import com.breuninger.boot.status.domain.Status;
import com.breuninger.boot.status.domain.StatusDetail;

public class CompositeStatusDetailIndicator implements StatusDetailIndicator {

  private final String name;
  private final List<StatusDetailIndicator> delegates;

  public CompositeStatusDetailIndicator(final String name, final List<StatusDetailIndicator> delegates) {
    this.name = name;
    this.delegates = delegates;
    if (delegates == null || delegates.isEmpty()) {
      throw new IllegalArgumentException("CompositeStatusDetailIndicator " + name + " does not have any delegate indicators");
    }
  }

  @Override
  public StatusDetail statusDetail() {
    if (delegates.size() == 1) {
      return delegates.get(0).statusDetail();
    } else {
      return StatusDetail.statusDetail(name, delegates.stream()
        .map(StatusDetailIndicator::statusDetails)
        .flatMap(Collection::stream)
        .map(StatusDetail::getStatus)
        .reduce(Status.OK, Status::plus), "Aggregated status of " + delegates.size() + " delegate indicators");
    }
  }

  @Override
  public List<StatusDetail> statusDetails() {
    return delegates.stream().map(StatusDetailIndicator::statusDetails).flatMap(Collection::stream).collect(toList());
  }
}
