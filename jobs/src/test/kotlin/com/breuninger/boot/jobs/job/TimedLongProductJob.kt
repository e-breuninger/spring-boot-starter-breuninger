package com.breuninger.boot.example.app.job

import com.breuninger.boot.jobs.JobRunnable
import com.breuninger.boot.jobs.domain.JobDefinition.Companion.timedFixedDelayJobDefinition
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.domain.JobMarker.JOB_MARKER
import com.breuninger.boot.jobs.domain.Timer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Duration.ofMillis
import kotlin.random.Random

@Component
class TimedLongProductJob : JobRunnable {

  companion object {

    val LOG: Logger = LoggerFactory.getLogger(TimedLongProductJob::class.java)
  }

  override fun definition() =
    timedFixedDelayJobDefinition(
      JobId(TimedLongProductJob::class.java.simpleName),
      TimedLongProductJob::class.java.simpleName,
      "",
      ofMillis(2000),
      Timer(name = "${TimedLongProductJob::class.java.name}.${actuatorEndpointPublicMethodName()}",
        histogram = true,
        percentiles = doubleArrayOf(0.5, 0.95),
        longTask = true))

  override fun execute() = logShitToConsole()

  override fun actuatorEndpointPublicMethodName() = this::logShitToConsole.name

  fun logShitToConsole(): Boolean {
    repeat(Random.nextInt(10, 60)) {
      LOG.info(JOB_MARKER, "LONG IT WORKS")
      Thread.sleep(1000)
    }
    return true
  }
}
