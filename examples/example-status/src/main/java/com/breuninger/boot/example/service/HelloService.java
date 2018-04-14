package com.breuninger.boot.example.service;

import static java.util.Collections.singletonMap;

import static com.breuninger.boot.status.domain.Status.OK;

import org.springframework.stereotype.Service;

import com.breuninger.boot.status.domain.StatusDetail;
import com.breuninger.boot.status.indicator.StatusDetailIndicator;

@Service
public class HelloService implements StatusDetailIndicator {

  public String getMessage() {
    return "Hello Breuninger Microservice!";
  }

  @Override
  public StatusDetail statusDetail() {
    return StatusDetail.statusDetail("HelloService", OK, "up and running", singletonMap("foo", "bar"));
  }
}
