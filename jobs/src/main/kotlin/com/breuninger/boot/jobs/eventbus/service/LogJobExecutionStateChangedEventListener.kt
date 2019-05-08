package com.breuninger.boot.jobs.eventbus.service

import com.breuninger.boot.jobs.eventbus.JobExecutionStateChangedEventListener
import com.breuninger.boot.jobs.eventbus.domain.JobExecutionStateChangedEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LogJobExecutionStateChangedEventListener : JobExecutionStateChangedEventListener {

  companion object {

    val LOG: Logger = LoggerFactory.getLogger(LogJobExecutionStateChangedEventListener::class.java)
  }

  override fun consumeJobExecutionStateChanged(event: JobExecutionStateChangedEvent) {
    LOG.info("${event.jobId} (${event.jobExecutionId}) state changed to ${event.state}")
  }
}
