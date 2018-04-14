package com.breuninger.boot.mongo.jobs;

import static java.time.OffsetDateTime.ofInstant;
import static java.time.ZoneId.systemDefault;
import static java.util.Date.from;

import java.time.OffsetDateTime;
import java.util.Date;

import lombok.experimental.UtilityClass;

@UtilityClass
class DateTimeConverters {

  static Date toDate(final OffsetDateTime offsetDateTime) {
    return from(offsetDateTime.toInstant());
  }

  static OffsetDateTime toOffsetDateTime(final Date date) {
    return date == null ? null : ofInstant(date.toInstant(), systemDefault());
  }
}
