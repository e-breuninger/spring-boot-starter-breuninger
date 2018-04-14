package com.breuninger.boot.status.indicator;

import static java.util.Objects.requireNonNull;

import net.jcip.annotations.ThreadSafe;

import com.breuninger.boot.status.domain.StatusDetail;

@ThreadSafe
public class MutableStatusDetailIndicator implements StatusDetailIndicator {

  private volatile StatusDetail statusDetail;

  public MutableStatusDetailIndicator(final StatusDetail initialStatusDetail) {
    statusDetail = requireNonNull(initialStatusDetail, "Initial StatusDetail must not be null");
  }

  @Override
  public StatusDetail statusDetail() {
    return statusDetail;
  }

  public void update(final StatusDetail statusDetail) {
    if (!this.statusDetail.getName().equals(statusDetail.getName())) {
      throw new IllegalArgumentException("Must not update StatusDetail with different names. That would be confusing.");
    }
    this.statusDetail = requireNonNull(statusDetail, "Parameter StatusDetail must not be null");
  }

  public void toOk(final String message) {
    update(statusDetail.toOk(message));
  }

  public void toWarning(final String message) {
    update(statusDetail.toWarning(message));
  }

  public void toError(final String message) {
    update(statusDetail.toError(message));
  }

  public void withDetail(final String key, final String value) {
    update(statusDetail.withDetail(key, value));
  }

  public void withoutDetail(final String key) {
    update(statusDetail.withoutDetail(key));
  }
}
