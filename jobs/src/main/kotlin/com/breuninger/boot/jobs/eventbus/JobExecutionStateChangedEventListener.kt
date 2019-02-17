package com.breuninger.boot.jobs.eventbus

import com.breuninger.boot.jobs.eventbus.domain.JobExecutionStateChangedEvent
import org.springframework.context.event.EventListener

interface JobExecutionStateChangedEventListener {

  @EventListener
  fun consumeJobExecutionStateChanged(jobExecutionStateChangedEvent: JobExecutionStateChangedEvent)
}
