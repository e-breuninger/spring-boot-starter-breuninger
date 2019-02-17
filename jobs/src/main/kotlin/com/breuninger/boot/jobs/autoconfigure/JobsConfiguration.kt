package com.breuninger.boot.jobs.autoconfigure

import com.breuninger.boot.jobs.repository.JobExecutionRepository
import com.breuninger.boot.jobs.repository.JobRepository
import com.breuninger.boot.jobs.repository.inmemory.InMemoryJobExecutionRepository
import com.breuninger.boot.jobs.repository.inmemory.InMemoryJobRepository
import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.concurrent.Executors.newScheduledThreadPool
import java.util.concurrent.ScheduledExecutorService

@Configuration
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties(JobsProperties::class)
@ConditionalOnProperty(prefix = "breuni.jobs", name = ["enabled"], havingValue = "true")
class JobsConfiguration(private val jobsProperties: JobsProperties) {

  @Bean
  @ConditionalOnMissingBean(ScheduledExecutorService::class)
  fun scheduledExecutorService(): ScheduledExecutorService =
    newScheduledThreadPool(jobsProperties.threadCount) { Thread(it) }

  @Bean
  fun timedAspect(meterRegistry: MeterRegistry) = TimedAspect(meterRegistry)

  @Bean
  @ConditionalOnMissingBean(JobRepository::class)
  fun jobRepository(): JobRepository = InMemoryJobRepository()

  @Bean
  @ConditionalOnMissingBean(JobExecutionRepository::class)
  fun jobExecutionRepository(): JobExecutionRepository = InMemoryJobExecutionRepository()
}
