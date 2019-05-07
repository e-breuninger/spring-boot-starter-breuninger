package com.breuninger.boot.jobs.eventbus.service

import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.eventbus.JobExecutionStateChangedEventListener
import com.breuninger.boot.jobs.eventbus.domain.JobExecutionStateChangedEvent
import com.breuninger.boot.jobs.eventbus.domain.JobExecutionStateChangedEvent.State.KEEP_ALIVE
import com.breuninger.boot.jobs.eventbus.domain.JobExecutionStateChangedEvent.State.RESTART
import com.breuninger.boot.jobs.eventbus.domain.JobExecutionStateChangedEvent.State.SKIPPED
import com.breuninger.boot.jobs.eventbus.domain.JobExecutionStateChangedEvent.State.START
import com.breuninger.boot.jobs.eventbus.domain.JobExecutionStateChangedEvent.State.STOP
import com.breuninger.boot.jobs.service.JobExecutionService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PersistJobExecutionStateChangedEventListener(
  private val jobExecutionService: JobExecutionService
) : JobExecutionStateChangedEventListener {

  companion object {

    val LOG: Logger = LoggerFactory.getLogger(
      PersistJobExecutionStateChangedEventListener::class.java)
  }

  override fun consumeJobExecutionStateChanged(event: JobExecutionStateChangedEvent) {
    val jobId = event.jobId
    val jobExecutionId = event.jobExecutionId
    try {
      when (event.state) {
        START -> jobExecutionService.save(JobExecution(jobExecutionId, jobId))
        KEEP_ALIVE -> jobExecutionService.keepAlive(jobExecutionId)
        RESTART -> jobExecutionService.markRestarted(jobExecutionId)
        SKIPPED -> {
          jobExecutionService.markSkipped(jobExecutionId)
          jobExecutionService.stop(jobId, jobExecutionId)
        }
        STOP -> jobExecutionService.stop(jobId, jobExecutionId)
      }
    } catch (exception: Exception) {
      LOG.error("Failed to persist job state change of $jobExecutionId to ${event.state}", exception)
    }
  }
}
