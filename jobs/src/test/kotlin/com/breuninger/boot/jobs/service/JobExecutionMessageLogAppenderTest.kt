package com.breuninger.boot.jobs.service

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import com.breuninger.boot.jobs.domain.JobExecutionMessage
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.domain.JobMarker
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.Before
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class JobExecutionMessageLogAppenderTest {

  private val jobExecutionService = mockk<JobExecutionService>()
  private val jobExecutionMessageLogAppender = JobExecutionMessageLogAppender(jobExecutionService)
  private val iLoggingEvent = mockk<ILoggingEvent>()
  private val logMessage = "foo bar bazz"
  val slot = slot<JobExecutionMessage>()

  @Before
  fun before() {
    every { iLoggingEvent.mdcPropertyMap } returns mapOf(
      "job_execution_id_value" to "bar"
    )
    val marker = JobMarker.jobMarkerFor(JobId("foo"))
    every { iLoggingEvent.marker } returns marker
    every { iLoggingEvent.formattedMessage } returns logMessage
    every { jobExecutionService.appendMessage(any(), capture(slot)) } returns Unit
  }

  @Test
  fun `ensure ILoggingEvent is appended correctly`() {
    every { iLoggingEvent.level } returns Level.INFO
    jobExecutionMessageLogAppender.doAppend(iLoggingEvent)
    //verify { jobExecutionService.appendMessage(JobExecutionId("bar"), JobExecutionMessage(any(), JobExecutionMessage.Level.INFO, logMessage)) }
    assertEquals(JobExecutionMessage.Level.INFO, slot.captured.level)
    assertEquals(logMessage, slot.captured.message)
  }
}
