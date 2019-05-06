package com.breuninger.boot.jobs.eventbus.service

import com.breuninger.boot.jobs.JobRunnable
import com.breuninger.boot.jobs.domain.JobDefinition
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.eventbus.domain.JobExecutionStateChangedEvent
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

// TODO implement
internal class MeterJobExecutionStateChangedEventListenerTest {

  private val meterRegistry = mockk<MeterRegistry>()
  private val meterJobExecutionStateChangedEventListener = MeterJobExecutionStateChangedEventListener(meterRegistry)
  private val jobRunnable = mockk<JobRunnable>()
  private val jobDefinition = mockk<JobDefinition>()

  @Test
  fun `ensure meterRegistry gauge function is called`() {
    every { jobRunnable.definition() } returns jobDefinition
    every { jobDefinition.jobId } returns JobId("foo")
    every { meterRegistry.gauge(JobExecutionStateChangedEvent::class.java.name, any(), 1) } returns 1

    val jobExecutionStateChangedEvent = JobExecutionStateChangedEvent(jobRunnable, JobExecutionId("bar"),
      JobExecutionStateChangedEvent.State.START)
    meterJobExecutionStateChangedEventListener.consumeJobExecutionStateChanged(jobExecutionStateChangedEvent)

    verify(exactly = 1) { meterRegistry.gauge(JobExecutionStateChangedEvent::class.java.name, any(), 1) }
  }
}
