package com.breuninger.boot.jobs

import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.service.JobService
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.config.CronTask
import org.springframework.scheduling.config.FixedDelayTask
import org.springframework.scheduling.config.FixedRateTask
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import org.springframework.stereotype.Component
import java.util.concurrent.ScheduledExecutorService

@Component
@ConditionalOnProperty(prefix = "breuni.jobs", name = ["enabled"], havingValue = "true")
class JobRegistrar(
  private val jobRunnables: List<JobRunnable>?,
  private val jobService: JobService,
  private val eventPublisher: ApplicationEventPublisher,
  private val scheduledExecutorService: ScheduledExecutorService,
  private val meterRegistry: MeterRegistry,
  private val jobExecutorRegistry: JobExecutorRegistry
) : SchedulingConfigurer {

  override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
    jobRunnables?.map {
      val jobDefinition = it.definition()
      jobService.create(Job(jobDefinition.jobId))
      val jobExecutor = JobExecutor(it, jobService, eventPublisher, scheduledExecutorService, meterRegistry)
      jobExecutorRegistry.register(jobDefinition.jobId, jobExecutor)
      jobDefinition.cron?.let { cron ->
        taskRegistrar.addCronTask(CronTask(jobExecutor, cron))
      }
      jobDefinition.fixedDelay?.let { fixedDelay ->
        taskRegistrar.addFixedDelayTask(FixedDelayTask(jobExecutor, fixedDelay.toMillis(), fixedDelay.toMillis()))
      }
      jobDefinition.fixedRate?.let { fixedRate ->
        taskRegistrar.addFixedRateTask(FixedRateTask(jobExecutor, fixedRate.toMillis(), fixedRate.toMillis()))
      }
    }
  }
}
