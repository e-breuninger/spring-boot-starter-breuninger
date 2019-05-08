package com.breuninger.boot.example.app.job

import com.breuninger.boot.jobs.JobRunnable
import com.breuninger.boot.jobs.domain.JobDefinition.Companion.manuallyTriggerableJobDefinition
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.domain.JobMarker.JOB_MARKER
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class ManualTriggeredJob : JobRunnable {

  companion object {

    val LOG: Logger = LoggerFactory.getLogger(ManualTriggeredJob::class.java)
  }

  override fun definition() =
    manuallyTriggerableJobDefinition(
      JobId(ManualTriggeredJob::class.java.simpleName),
      ManualTriggeredJob::class.java.simpleName,
      "some manually triggerable job")

  override fun execute() = logShitToConsole()

  override fun actuatorEndpointPublicMethodName() = this::logShitToConsole.name

  fun logShitToConsole(): Boolean {
    repeat(Random.nextInt(10, 60)) {
      LOG.info(JOB_MARKER, "MANUAL IT WORKS")
      Thread.sleep(1000)
    }
    return true
  }
}
