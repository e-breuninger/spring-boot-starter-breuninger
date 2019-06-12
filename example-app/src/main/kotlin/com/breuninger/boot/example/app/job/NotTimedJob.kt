package com.breuninger.boot.example.app.job

import com.breuninger.boot.jobs.JobRunnable
import com.breuninger.boot.jobs.domain.JobDefinition.Companion.fixedDelayJobDefinition
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.domain.JobMarker.JOB_MARKER
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Duration.ofMillis

@Component
class NotTimedJob : JobRunnable {

  companion object {

    private val LOG: Logger = LoggerFactory.getLogger(NotTimedJob::class.java)
  }

  override fun definition() =
    fixedDelayJobDefinition(
      JobId(NotTimedJob::class.java.simpleName),
      NotTimedJob::class.java.simpleName,
      "",
      ofMillis(2000))

  override fun execute() = logShitToConsole()

  override fun actuatorEndpointPublicMethodName() = this::logShitToConsole.name

  fun logShitToConsole(): Boolean {
    LOG.info(JOB_MARKER, "IT WORKS")
    return true
  }
}
