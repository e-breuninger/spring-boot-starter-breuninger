package com.breuninger.boot.jobs.eventbus.service

import com.breuninger.boot.jobs.eventbus.JobExecutionStateChangedEventListener
import com.breuninger.boot.jobs.eventbus.domain.JobExecutionStateChangedEvent
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag

class MeterJobExecutionStateChangedEventListener(
  private val meterRegistry: MeterRegistry
) : JobExecutionStateChangedEventListener {

  override fun consumeJobExecutionStateChanged(jobExecutionStateChangedEvent: JobExecutionStateChangedEvent) {
    meterRegistry.gauge(JobExecutionStateChangedEvent::class.java.name,
      listOf(
        Tag.of("job_id", jobExecutionStateChangedEvent.jobId.value),
        Tag.of("job_execution_id", jobExecutionStateChangedEvent.jobExecutionId.value),
        Tag.of("state", jobExecutionStateChangedEvent.state.name)
      ),
      1)
  }
}
