package com.breuninger.boot.jobs.service

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobExecutionMessage
import com.breuninger.boot.jobs.domain.JobExecutionMessage.Level.ERROR
import com.breuninger.boot.jobs.domain.JobExecutionMessage.Level.INFO
import com.breuninger.boot.jobs.domain.JobExecutionMessage.Level.WARNING
import com.breuninger.boot.jobs.domain.JobMarker.JOB_MARKER
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.time.Instant.now

@Component
@ConditionalOnProperty(prefix = "breuni.jobs", name = ["enabled"], havingValue = "true")
final class JobExecutionMessageLogAppender(private val jobExecutionService: JobExecutionService) : AppenderBase<ILoggingEvent>() {

  init {
    val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    setContext(loggerContext)
    start()
    loggerContext.getLogger("ROOT").addAppender(this)
  }

  override fun append(eventObject: ILoggingEvent) {
    val jobExecutionIdValue = eventObject.mdcPropertyMap["job_execution_id_value"]
    if (jobExecutionIdValue != null && eventObject.marker != null && JOB_MARKER.contains(eventObject.marker)) {
      val logMessage = eventObject.formattedMessage
      try {
        val level = jobExecutionMessageLevel(eventObject.level)
        val message = JobExecutionMessage(now(), level, logMessage)
        jobExecutionService.appendMessage(JobExecutionId(jobExecutionIdValue), message)
      } catch (exception: Exception) {
        addError("Failed to persist job execution $jobExecutionIdValue message $logMessage", exception)
      }
    }
  }

  private fun jobExecutionMessageLevel(level: Level) = when (level.levelStr) {
    "ERROR" -> ERROR
    "WARN" -> WARNING
    else -> INFO
  }
}
