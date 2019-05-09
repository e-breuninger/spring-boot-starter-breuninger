package com.breuninger.boot.jobs

import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.job.CronJob
import com.breuninger.boot.jobs.job.FixedDelayJob
import com.breuninger.boot.jobs.job.FixedRateJob
import com.breuninger.boot.jobs.job.ManualTriggeredJob
import com.breuninger.boot.jobs.repository.JobExecutorRegistry
import com.breuninger.boot.jobs.service.JobExecutionService
import com.breuninger.boot.jobs.service.JobService
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import java.util.concurrent.ScheduledExecutorService

internal class JobRegistrarTest {

  val jobRunnables = listOf(CronJob(), ManualTriggeredJob(), FixedRateJob(), FixedDelayJob())
  val jobService = mockk<JobService>()
  val jobExecutionService = mockk<JobExecutionService>()
  val applicationEventPublisher = mockk<ApplicationEventPublisher>()
  val scheduledExecutorService = mockk<ScheduledExecutorService>()
  val meterRegistry = mockk<MeterRegistry>()
  val jobExecutorRegistry = mockk<JobExecutorRegistry>()
  val taskRegistrar = mockk<ScheduledTaskRegistrar>()
  val jobRegistrar = JobRegistrar(jobRunnables, jobService, jobExecutionService, applicationEventPublisher, scheduledExecutorService, meterRegistry, jobExecutorRegistry)

  @BeforeEach
  fun beforeEach() {
    every { jobService.create(any()) } returns Unit
    every { jobExecutorRegistry.register(any(), any()) } returns Unit
    every { taskRegistrar.addCronTask(any()) } returns Unit
    every { taskRegistrar.addFixedDelayTask(any()) } returns Unit
    every { taskRegistrar.addFixedRateTask(any()) } returns Unit
  }

  @Test
  fun `ensure that the correct methods of the ScheduledTaskRegistrar are called`() {
    jobRegistrar.configureTasks(taskRegistrar)

    verifyAll {
      taskRegistrar.addCronTask(any())
      taskRegistrar.addFixedDelayTask(any())
      taskRegistrar.addFixedRateTask(any())
    }
  }

  @Test
  fun `ensure that JobService create is called for al four jobs`() {
    jobRegistrar.configureTasks(taskRegistrar)
    verifyAll {
      jobService.create(Job(CronJob().definition().jobId))
      jobService.create(Job(ManualTriggeredJob().definition().jobId))
      jobService.create(Job(FixedRateJob().definition().jobId))
      jobService.create(Job(FixedDelayJob().definition().jobId))
    }
  }

  @Test
  fun `ensure that JobExecutorRegistry register is called for all four jobs`() {
    jobRegistrar.configureTasks(taskRegistrar)
    verifyAll {
      jobExecutorRegistry.register(CronJob().definition().jobId, any())
      jobExecutorRegistry.register(ManualTriggeredJob().definition().jobId, any())
      jobExecutorRegistry.register(FixedRateJob().definition().jobId, any())
      jobExecutorRegistry.register(FixedDelayJob().definition().jobId, any())
    }
  }
}
