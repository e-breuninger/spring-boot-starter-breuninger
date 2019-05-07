package com.breuninger.boot.jobs.service

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.LoggingEvent
import com.breuninger.boot.jobs.domain.JobExecutionMessage
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.domain.JobMarker
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

internal class JobExecutionMessageLogAppenderTest {

  private val jobExecutionService = mockk<JobExecutionService>()
  private val jobExecutionMessageLogAppender = JobExecutionMessageLogAppender(jobExecutionService)
  private val iLoggingEvent = mockk<LoggingEvent>()
  private val logMessage = "foo bar bazz"
  val slot = slot<JobExecutionMessage>()

  companion object {
    @JvmStatic
    private fun testDataProvider() = listOf(
      TestData(Level.INFO, JobExecutionMessage.Level.INFO),
      TestData(Level.ALL, JobExecutionMessage.Level.INFO),
      TestData(Level.DEBUG, JobExecutionMessage.Level.INFO),
      TestData(Level.ERROR, JobExecutionMessage.Level.ERROR),
      TestData(Level.OFF, JobExecutionMessage.Level.INFO),
      TestData(Level.TRACE, JobExecutionMessage.Level.INFO),
      TestData(Level.WARN, JobExecutionMessage.Level.WARNING)
    )
  }

  data class TestData(
    val iLoggingEventLevel: Level,
    val jobExecutionMessageLevel: JobExecutionMessage.Level
  )

  @BeforeEach
  fun before() {
    every { iLoggingEvent.mdcPropertyMap } returns mapOf(
      "job_execution_id_value" to "bar"
    )
    val marker = JobMarker.jobMarkerFor(JobId("foo"))
    every { iLoggingEvent.marker } returns marker
    every { iLoggingEvent.formattedMessage } returns logMessage
    every { jobExecutionService.appendMessage(any(), capture(slot)) } returns Unit
  }

  @ParameterizedTest
  @MethodSource("testDataProvider")
  fun `ensure ILoggingEvent with Level INFO is appended correctly`(testData: TestData) {
    every { iLoggingEvent.level } returns testData.iLoggingEventLevel
    jobExecutionMessageLogAppender.doAppend(iLoggingEvent)
    assertEquals(testData.jobExecutionMessageLevel, slot.captured.level)
    assertEquals(logMessage, slot.captured.message)
  }
}
