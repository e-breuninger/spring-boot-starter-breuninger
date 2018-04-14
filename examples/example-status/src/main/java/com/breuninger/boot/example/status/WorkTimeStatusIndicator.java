package com.breuninger.boot.example.status;

import static java.time.LocalTime.now;

import static com.breuninger.boot.status.domain.Status.OK;
import static com.breuninger.boot.status.domain.Status.WARNING;

import java.time.LocalTime;

import org.springframework.stereotype.Component;

import com.breuninger.boot.status.domain.StatusDetail;
import com.breuninger.boot.status.indicator.StatusDetailIndicator;

@Component
public class WorkTimeStatusIndicator implements StatusDetailIndicator {

  @Override
  public StatusDetail statusDetail() {
    if (isWorkingTime(now())) {
      return StatusDetail.statusDetail("Time to work", OK, "go ahead");
    } else {
      return StatusDetail.statusDetail("Time to work", WARNING, "go home now");
    }
  }

  private boolean isWorkingTime(final LocalTime time) {
    return time.getHour() >= 9 && time.getHour() < 18;
  }
}
