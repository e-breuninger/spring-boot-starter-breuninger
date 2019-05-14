package com.breuninger.boot.jobs.job

import com.breuninger.boot.jobs.JobRunnable
import com.breuninger.boot.jobs.domain.JobDefinition.Companion.timedFixedDelayJobDefinition
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.domain.JobMarker.JOB_MARKER
import com.breuninger.boot.jobs.domain.Timer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration.ofMillis
import kotlin.random.Random

class FixedDelayJob : JobRunnable {

  companion object {

    val LOG: Logger = LoggerFactory.getLogger(FixedDelayJob::class.java)
  }

  override fun definition() =
    timedFixedDelayJobDefinition(
      JobId(FixedDelayJob::class.java.simpleName),
      FixedDelayJob::class.java.simpleName,
      "",
      ofMillis(2000),
      Timer(name = "${FixedDelayJob::class.java.name}.${actuatorEndpointPublicMethodName()}"))

  override fun execute() = logShitToConsole()

  override fun actuatorEndpointPublicMethodName() = this::logShitToConsole.name

  private fun logShitToConsole(): Boolean {
    repeat(Random.nextInt(10, 60)) {
      LOG.info(JOB_MARKER, "SHORT IT WORKS")
      Thread.sleep(1000)
    }
    return true
  }
}
