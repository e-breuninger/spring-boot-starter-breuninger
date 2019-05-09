package com.breuninger.boot.jobs

import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.eventbus.domain.JobExecutionStateChangedEvent
import com.breuninger.boot.jobs.job.CronJob
import com.breuninger.boot.jobs.service.JobExecutionService
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture

internal class JobExecutorTest{

  private val jobRunnable = spyk(CronJob())
  private val jobExecutionService = mockk<JobExecutionService>()
  private val applicationEventPublisher = mockk<ApplicationEventPublisher>()
  val scheduledExecutorService = mockk<ScheduledExecutorService>()
  val meterRegistry = mockk<MeterRegistry>()
  private val jobExecutor = JobExecutor(jobRunnable,jobExecutionService,applicationEventPublisher,scheduledExecutorService,meterRegistry)

  @BeforeEach
  fun beforeEach(){
    every { jobExecutionService.acquireRunLock(any(),any()) } returns Job(jobRunnable.definition().jobId)
    every { applicationEventPublisher.publishEvent(any< JobExecutionStateChangedEvent>()) } returns Unit
    every { scheduledExecutorService.scheduleAtFixedRate(any(),any(),any(),any()) } returns mockk<ScheduledFuture<String>>()
  }

  @Test
  fun `ensure that JobExecutionService acquirceRunLock is called on run`(){
    jobExecutor.run()
    verify { jobExecutionService.acquireRunLock(any(),any()) }
  }

  @Test
  fun `ensure that JobRunnable execute is called on run`(){
    jobExecutor.run()
    verify { jobRunnable.execute() }
  }
}
