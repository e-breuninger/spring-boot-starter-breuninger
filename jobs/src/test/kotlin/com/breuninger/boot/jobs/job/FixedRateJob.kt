package com.breuninger.boot.jobs.job

import com.breuninger.boot.jobs.JobRunnable
import com.breuninger.boot.jobs.domain.JobDefinition.Companion.timedFixedRateJobDefinition
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.domain.JobMarker.JOB_MARKER
import com.breuninger.boot.jobs.domain.Timer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import kotlin.random.Random

class FixedRateJob : JobRunnable {

  companion object {

    val LOG: Logger = LoggerFactory.getLogger(FixedRateJob::class.java)
  }

  override fun definition() =
    timedFixedRateJobDefinition(
      JobId(FixedRateJob::class.java.simpleName),
      FixedRateJob::class.java.simpleName,
      "",
      Duration.ofMillis(2000),
      Timer())

  override fun execute() = logShitToConsole()

  override fun actuatorEndpointPublicMethodName() = this::logShitToConsole.name

  private fun logShitToConsole(): Boolean {
    repeat(Random.nextInt(10, 60)) {
      LOG.info(JOB_MARKER, "LONG IT WORKS")
      Thread.sleep(1000)
    }
    return true
  }
}
