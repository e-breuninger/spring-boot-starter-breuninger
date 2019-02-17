package com.breuninger.boot.jobs.eventbus.domain

import com.breuninger.boot.jobs.JobRunnable
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobId
import org.springframework.context.ApplicationEvent

class JobExecutionStateChangedEvent private constructor(
  jobRunnable: JobRunnable,
  val jobId: JobId,
  val jobExecutionId: JobExecutionId,
  val state: State
) : ApplicationEvent(jobRunnable) {

  constructor(
    jobRunnable: JobRunnable,
    jobExecutionId: JobExecutionId,
    state: State
  ) : this(jobRunnable, jobRunnable.definition().jobId, jobExecutionId, state)

  enum class State {

    START,
    KEEP_ALIVE,
    RESTART,
    SKIPPED,
    STOP
  }
}
