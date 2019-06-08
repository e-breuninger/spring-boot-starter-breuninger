package com.breuninger.boot.jobs.job

import com.breuninger.boot.jobs.JobRunnable
import com.breuninger.boot.jobs.domain.JobDefinition.Companion.cronJobDefinition
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.domain.JobMarker.JOB_MARKER
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CronJob : JobRunnable {

  companion object {

    val LOG: Logger = LoggerFactory.getLogger(CronJob::class.java)
  }

  override fun definition() =
    cronJobDefinition(
      JobId(CronJob::class.java.simpleName),
      CronJob::class.java.simpleName,
      "cron",
      "0 0 * * * *")

  override fun execute() = logShitToConsole()

  override fun actuatorEndpointPublicMethodName() = this::logShitToConsole.name

  fun logShitToConsole(): Boolean {
    LOG.info(JOB_MARKER, "IT WORKS")
    return true
  }
}
