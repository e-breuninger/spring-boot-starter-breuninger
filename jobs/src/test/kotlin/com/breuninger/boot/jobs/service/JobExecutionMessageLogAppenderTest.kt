package com.breuninger.boot.jobs.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.LoggingEvent
import com.breuninger.boot.jobs.domain.JobExecutionMessage
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.domain.JobMarker.jobMarkerFor
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class JobExecutionMessageLogAppenderTest {

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

  private val iLoggingEvent = mockk<LoggingEvent>()
  private val logMessage = "foo bar bazz"
  private val slot = slot<JobExecutionMessage>()
  private val jobExecutionService = mockk<JobExecutionService>()
  private val jobExecutionMessageLogAppender = JobExecutionMessageLogAppender(jobExecutionService)

  @BeforeEach
  fun before() {
    every { iLoggingEvent.mdcPropertyMap } returns mapOf("job_execution_id_value" to "bar")
    every { iLoggingEvent.marker } returns jobMarkerFor(JobId("foo"))
    every { iLoggingEvent.formattedMessage } returns logMessage
    every { jobExecutionService.appendMessage(any(), capture(slot)) } returns Unit
  }

  @ParameterizedTest
  @MethodSource("testDataProvider")
  fun `ensure ILoggingEvent with Level INFO is appended correctly`(testData: TestData) {
    every { iLoggingEvent.level } returns testData.iLoggingEventLevel
    jobExecutionMessageLogAppender.doAppend(iLoggingEvent)

    assertThat(slot.captured.level).isEqualTo(testData.jobExecutionMessageLevel)
    assertThat(slot.captured.message).isEqualTo(logMessage)
  }

  data class TestData(
    val iLoggingEventLevel: Level,
    val jobExecutionMessageLevel: JobExecutionMessage.Level
  )
}
