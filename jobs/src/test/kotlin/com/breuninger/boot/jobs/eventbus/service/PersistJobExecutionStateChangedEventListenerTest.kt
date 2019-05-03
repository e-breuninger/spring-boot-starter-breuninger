package com.breuninger.boot.jobs.eventbus.service

import com.breuninger.boot.jobs.JobRunnable
import com.breuninger.boot.jobs.domain.JobDefinition
import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.eventbus.domain.JobExecutionStateChangedEvent
import com.breuninger.boot.jobs.service.JobExecutionService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

// TODO implement
internal class PersistJobExecutionStateChangedEventListenerTest {

  private val jobExecutionService = mockk<JobExecutionService>()
  private val jobRunnable = mockk<JobRunnable>()
  private val jobDefinition = mockk<JobDefinition>()
  private val jobExecutionId = JobExecutionId("bar")
  private val jobId = JobId("foo")
  private val persistJobExecutionStateChangedEventListener = PersistJobExecutionStateChangedEventListener(jobExecutionService)

  @BeforeEach
  fun before() {
    every { jobRunnable.definition() } returns jobDefinition
    every { jobDefinition.jobId } returns jobId
    every { jobExecutionService.markRestarted(jobExecutionId) } returns Unit
    every { jobExecutionService.markSkipped(jobExecutionId) } returns Unit
    every { jobExecutionService.keepAlive(jobExecutionId) } returns Unit
    every { jobExecutionService.stop(jobId, jobExecutionId) } returns Unit
    every { jobExecutionService.save(any()) } returns JobExecution(jobExecutionId, jobId)
  }

  @Test
  fun `ensure jobExecutionService save is called once`() {
    val jobExecutionStateChangedEvent = createJobExecutionStateChangedEvent(JobExecutionStateChangedEvent.State.START)

    persistJobExecutionStateChangedEventListener.consumeJobExecutionStateChanged(jobExecutionStateChangedEvent)

    verify(exactly = 1) { jobExecutionService.save(any()) }
  }

  @Test
  fun `ensure jobExecutionService keepAlive is called once`() {
    val jobExecutionStateChangedEvent = createJobExecutionStateChangedEvent(JobExecutionStateChangedEvent.State.KEEP_ALIVE)

    persistJobExecutionStateChangedEventListener.consumeJobExecutionStateChanged(jobExecutionStateChangedEvent)

    verify(exactly = 1) { jobExecutionService.keepAlive(jobExecutionId) }
  }

  @Test
  fun `ensure jobExecutionService markRestarted is called once`() {
    val jobExecutionStateChangedEvent = createJobExecutionStateChangedEvent(JobExecutionStateChangedEvent.State.RESTART)

    persistJobExecutionStateChangedEventListener.consumeJobExecutionStateChanged(jobExecutionStateChangedEvent)

    verify(exactly = 1) { jobExecutionService.markRestarted(jobExecutionId) }
  }

  @Test
  fun `ensure jobExecutionService markSkipped and stop are called once`() {
    val jobExecutionStateChangedEvent = createJobExecutionStateChangedEvent(JobExecutionStateChangedEvent.State.SKIPPED)

    persistJobExecutionStateChangedEventListener.consumeJobExecutionStateChanged(jobExecutionStateChangedEvent)

    verifySequence {
      jobExecutionService.markSkipped(jobExecutionId)
      jobExecutionService.stop(jobId, jobExecutionId)
    }
  }

  @Test
  fun `ensure jobExecutionService stop is called once`() {
    val jobExecutionStateChangedEvent = createJobExecutionStateChangedEvent(JobExecutionStateChangedEvent.State.STOP)

    persistJobExecutionStateChangedEventListener.consumeJobExecutionStateChanged(jobExecutionStateChangedEvent)

    verify(exactly = 1) { jobExecutionService.stop(jobId, jobExecutionId) }
  }

  private fun createJobExecutionStateChangedEvent(state: JobExecutionStateChangedEvent.State) =
    JobExecutionStateChangedEvent(jobRunnable, jobExecutionId, state)
}
