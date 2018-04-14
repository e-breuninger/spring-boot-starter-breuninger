package com.breuninger.boot.status.indicator;

import static java.util.Collections.singletonList;

import java.util.List;

import com.breuninger.boot.status.domain.StatusDetail;

public interface StatusDetailIndicator {

  StatusDetail statusDetail();

  default List<StatusDetail> statusDetails() {
    return singletonList(statusDetail());
  }
}
