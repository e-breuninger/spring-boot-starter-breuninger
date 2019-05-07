package com.breuninger.boot.jobs.eventbus.service

import com.breuninger.boot.jobs.eventbus.JobExecutionStateChangedEventListener
import com.breuninger.boot.jobs.eventbus.domain.JobExecutionStateChangedEvent
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag

class MeterJobExecutionStateChangedEventListener(
  private val meterRegistry: MeterRegistry
) : JobExecutionStateChangedEventListener {

  override fun consumeJobExecutionStateChanged(event: JobExecutionStateChangedEvent) {
    meterRegistry.gauge(JobExecutionStateChangedEvent::class.java.name,
      listOf(
        Tag.of("job_id", event.jobId.value),
        Tag.of("job_execution_id", event.jobExecutionId.value),
        Tag.of("state", event.state.name)
      ),
      1)
  }
}
