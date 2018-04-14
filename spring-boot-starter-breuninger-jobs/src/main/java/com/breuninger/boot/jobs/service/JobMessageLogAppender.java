package com.breuninger.boot.jobs.service;

import static com.breuninger.boot.jobs.domain.JobMessage.jobMessage;

import java.time.OffsetDateTime;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.breuninger.boot.jobs.domain.JobMarker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

@Component
public class JobMessageLogAppender extends AppenderBase<ILoggingEvent> {

  private final JobService jobService;

  public JobMessageLogAppender(final JobService jobService) {
    this.jobService = jobService;

    final var lc = (LoggerContext) LoggerFactory.getILoggerFactory();
    setContext(lc);
    start();

    lc.getLogger("ROOT").addAppender(this);
  }

  @Override
  protected void append(final ILoggingEvent eventObject) {
    final var mdcMap = eventObject.getMDCPropertyMap();
    // TODO: check for JOB marker:
    if (mdcMap.containsKey("job_id") && eventObject.getMarker() != null && JobMarker.JOB.contains(eventObject.getMarker())) {
      final var jobId = mdcMap.get("job_id");
      final var level = eventObject.getLevel();
      final var breuningerLevel = logLevelToBreuningerLevel(level);

      final var message = eventObject.getFormattedMessage();

      try {
        final var jobMessage = jobMessage(breuningerLevel, message, OffsetDateTime.now());
        jobService.appendMessage(jobId, jobMessage);
      } catch (final RuntimeException e) {
        addError("Failed to persist job message (jobId=" + jobId + "): " + message, e);
      }
    }
  }

  private com.breuninger.boot.jobs.domain.Level logLevelToBreuningerLevel(final Level level) {
    switch (level.levelStr) {
      case "ERROR":
        return com.breuninger.boot.jobs.domain.Level.ERROR;
      case "WARN":
        return com.breuninger.boot.jobs.domain.Level.WARNING;
      default:
        return com.breuninger.boot.jobs.domain.Level.INFO;
    }
  }
}
