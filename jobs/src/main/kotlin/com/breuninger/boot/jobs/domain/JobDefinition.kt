package com.breuninger.boot.jobs.domain

import java.time.Duration

class JobDefinition private constructor(
  val jobId: JobId,
  val name: String,
  val description: String,
  val cron: String?,
  val fixedDelay: Duration?,
  val fixedRate: Duration?,
  val restarts: Int,
  val restartDelay: Duration?,
  val timer: Timer?
) {

  companion object {

    fun manuallyTriggerableJobDefinition(
      jobId: JobId,
      name: String,
      description: String
    ) = JobDefinition(jobId, name, description, null, null, null, 0, null, null)

    fun timedManuallyTriggerableJobDefinition(
      jobId: JobId,
      name: String,
      description: String,
      timer: Timer
    ) = JobDefinition(jobId, name, description, null, null, null, 0, null, timer)

    fun cronJobDefinition(
      jobId: JobId,
      name: String,
      description: String,
      cron: String
    ) = JobDefinition(jobId, name, description, cron, null, null, 0, null, null)

    fun timedCronJobDefinition(
      jobId: JobId,
      name: String,
      description: String,
      cron: String,
      timer: Timer
    ) = JobDefinition(jobId, name, description, cron, null, null, 0, null, timer)

    fun restartableCronJobDefinition(
      jobId: JobId,
      name: String,
      description: String,
      cron: String,
      restarts: Int,
      restartDelay: Duration
    ) = JobDefinition(jobId, name, description, cron, null, null, restarts, restartDelay, null)

    fun timedRestartableCronJobDefinition(
      jobId: JobId,
      name: String,
      description: String,
      cron: String,
      restarts: Int,
      restartDelay: Duration,
      timer: Timer
    ) = JobDefinition(jobId, name, description, cron, null, null, restarts, restartDelay, timer)

    fun fixedDelayJobDefinition(
      jobId: JobId,
      name: String,
      description: String,
      fixedDelay: Duration
    ) = JobDefinition(jobId, name, description, null, fixedDelay, null, 0, null, null)

    fun timedFixedDelayJobDefinition(
      jobId: JobId,
      name: String,
      description: String,
      fixedDelay: Duration,
      timer: Timer
    ) = JobDefinition(jobId, name, description, null, fixedDelay, null, 0, null, timer)

    fun restartableFixedDelayJobDefinition(
      jobId: JobId,
      name: String,
      description: String,
      fixedDelay: Duration,
      restarts: Int,
      restartDelay: Duration
    ) = JobDefinition(jobId, name, description, null, fixedDelay, null, restarts, restartDelay, null)

    fun timedRestartableFixedDelayJobDefinition(
      jobId: JobId,
      name: String,
      description: String,
      fixedDelay: Duration,
      restarts: Int,
      restartDelay: Duration,
      timer: Timer
    ) = JobDefinition(jobId, name, description, null, fixedDelay, null, restarts, restartDelay, timer)

    fun fixedRateJobDefinition(
      jobId: JobId,
      name: String,
      description: String,
      fixedRate: Duration
    ) = JobDefinition(jobId, name, description, null, null, fixedRate, 0, null, null)

    fun timedFixedRateJobDefinition(
      jobId: JobId,
      name: String,
      description: String,
      fixedRate: Duration,
      timer: Timer
    ) = JobDefinition(jobId, name, description, null, null, fixedRate, 0, null, timer)

    fun restartableFixedRateJobDefinition(
      jobId: JobId,
      name: String,
      description: String,
      fixedRate: Duration,
      restarts: Int,
      restartDelay: Duration
    ) = JobDefinition(jobId, name, description, null, null, fixedRate, restarts, restartDelay, null)

    fun timedRestartableFixedRateJobDefinition(
      jobId: JobId,
      name: String,
      description: String,
      fixedRate: Duration,
      restarts: Int,
      restartDelay: Duration,
      timer: Timer
    ) = JobDefinition(jobId, name, description, null, null, fixedRate, restarts, restartDelay, timer)
  }
}
