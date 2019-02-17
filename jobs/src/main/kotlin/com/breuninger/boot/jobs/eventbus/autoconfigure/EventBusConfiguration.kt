package com.breuninger.boot.jobs.eventbus.autoconfigure

import com.breuninger.boot.jobs.eventbus.service.LogJobExecutionStateChangedEventListener
import com.breuninger.boot.jobs.eventbus.service.MeterJobExecutionStateChangedEventListener
import com.breuninger.boot.jobs.eventbus.service.PersistJobExecutionStateChangedEventListener
import com.breuninger.boot.jobs.service.JobExecutionService
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(prefix = "breuni.jobs", name = ["enabled"], havingValue = "true")
class EventBusConfiguration {

  @Bean
  fun logJobExecutionStateChangedEventListener() = LogJobExecutionStateChangedEventListener()

  @Bean
  fun persistJobExecutionStateChangedEventListener(jobExecutionService: JobExecutionService) =
    PersistJobExecutionStateChangedEventListener(jobExecutionService)

  @Bean
  fun meterJobExecutionStateChangedEventListener(meterRegistry: MeterRegistry) =
    MeterJobExecutionStateChangedEventListener(meterRegistry)
}
