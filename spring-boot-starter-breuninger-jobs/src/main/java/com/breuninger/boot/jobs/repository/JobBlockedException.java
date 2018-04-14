package com.breuninger.boot.jobs.repository;

public class JobBlockedException extends RuntimeException {

  public JobBlockedException(final String message) {
    super(message);
  }
}
