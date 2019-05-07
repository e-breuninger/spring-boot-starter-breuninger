package com.breuninger.boot.jobs.repository

import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobExecution.Status.OK
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobExecutionMessage
import com.breuninger.boot.jobs.domain.JobId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.random.Random

abstract class AbstractJobExecutionRepositoryTest {

  @Test
  fun `ensure saving and finding the saved item works`() {
    val jobExecutionFoo = createJobExecution()
    val jobExecutionBar = createJobExecution(id = JobExecutionId("bar"))
    val jobExecutionBat = createJobExecution(id = JobExecutionId("bat"))
    getRepository().save(jobExecutionFoo)
    getRepository().save(jobExecutionBar)
    getRepository().save(jobExecutionBat)

    assertEquals(jobExecutionFoo, getRepository().findOne(JobExecutionId("foo")))
    assertEquals(jobExecutionBar, getRepository().findOne(JobExecutionId("bar")))
    assertEquals(jobExecutionBat, getRepository().findOne(JobExecutionId("bat")))
  }

  @Test
  fun `ensure finding the last hundred jobs sorted in descending order works`() {
    for (i in 1..150) {
      getRepository().save(createJobExecution(
        id = JobExecutionId(i.toString()),
        lastUpdated = Instant.ofEpochSecond(Random.nextLong(
          Instant.now().minusSeconds(5000).epochSecond,
          Instant.now().epochSecond))))
    }

    val jobExecutions = getRepository().find100DescendingByLastUpdated(null)

    assertEquals(100, jobExecutions.size)
    var lastInstant: Long = Long.MAX_VALUE
    for (jobExecution in jobExecutions) {
      assert(jobExecution.lastUpdated.epochSecond <= lastInstant)
      lastInstant = jobExecution.lastUpdated.epochSecond
    }
  }

  @Test
  fun `ensure finding the last hundred jobs with the given jobId sorted in descending order works`() {
    for (i in 1..300) {
      var jobId = JobId("foo")
      if (i % 2 == 0) {
        jobId = JobId("bar")
      }
      getRepository().save(
        createJobExecution(
          id = JobExecutionId(i.toString()),
          jobId = jobId,
          lastUpdated = Instant.ofEpochSecond(Random.nextLong(
            Instant.now().minusSeconds(5000).epochSecond,
            Instant.now().epochSecond))))
    }

    val jobExecutions = getRepository().find100DescendingByLastUpdated(JobId("foo"))

    assertEquals(100, jobExecutions.size)
    var lastInstant: Long = Long.MAX_VALUE
    for (jobExecution in jobExecutions) {
      assert(jobExecution.lastUpdated.epochSecond <= lastInstant)
      assertEquals(JobId("foo"), jobExecution.jobId)
      lastInstant = jobExecution.lastUpdated.epochSecond
    }
  }

  @Test
  fun `ensure finding all ignoring messages works`() {
    for (i in 1..300) {
      getRepository().save(createJobExecution(
        id = JobExecutionId(i.toString()),
        messages = listOf(JobExecutionMessage(Instant.now(), JobExecutionMessage.Level.ERROR, "foo bar"))))
    }

    val jobExecutions = getRepository().findAllIgnoreMessages()

    assertEquals(300, jobExecutions.size)
    for (jobExecution in jobExecutions) {
      assert(jobExecution.messages.isEmpty())
    }
  }

  @Test
  fun `ensure remove works`() {
    val jobExecutionFoo = createJobExecution()
    val jobExecutionBar = createJobExecution(id = JobExecutionId("bar"))
    val jobExecutionBat = createJobExecution(id = JobExecutionId("bat"))
    getRepository().save(jobExecutionFoo)
    getRepository().save(jobExecutionBar)
    getRepository().save(jobExecutionBat)

    getRepository().remove(jobExecutionFoo)
    getRepository().remove(jobExecutionBar)
    getRepository().remove(jobExecutionBat)

    assertNull(getRepository().findOne(JobExecutionId("foo")))
    assertNull(getRepository().findOne(JobExecutionId("bar")))
    assertNull(getRepository().findOne(JobExecutionId("bat")))
  }

  @Test
  fun `ensure stop works`() {
    val jobExecutionFoo = createJobExecution()
    getRepository().save(jobExecutionFoo)

    assertNull(jobExecutionFoo.stopped)

    getRepository().stop(JobExecutionId("foo"), Instant.now())

    assertNotNull(getRepository().findOne(JobExecutionId("foo"))?.stopped)
  }

  @Test
  fun `ensure updateStatus works`() {
    val jobExecutionFoo = createJobExecution()
    getRepository().save(jobExecutionFoo)

    assertEquals(OK, jobExecutionFoo.status)

    getRepository().updateStatus(JobExecutionId("foo"), JobExecution.Status.DEAD)

    assertEquals(JobExecution.Status.DEAD, getRepository().findOne(JobExecutionId("foo"))?.status)

    getRepository().updateStatus(JobExecutionId("foo"), JobExecution.Status.ERROR)

    assertEquals(JobExecution.Status.ERROR, getRepository().findOne(JobExecutionId("foo"))?.status)

    getRepository().updateStatus(JobExecutionId("foo"), JobExecution.Status.SKIPPED)

    assertEquals(JobExecution.Status.SKIPPED, getRepository().findOne(JobExecutionId("foo"))?.status)

    getRepository().updateStatus(JobExecutionId("foo"), OK)

    assertEquals(OK, getRepository().findOne(JobExecutionId("foo"))?.status)
  }

  @Test
  fun `ensure appendMessage works`() {
    val jobExecutionFoo = createJobExecution()
    getRepository().save(jobExecutionFoo)

    assert(jobExecutionFoo.messages.isEmpty())

    getRepository().appendMessage(JobExecutionId("foo"),
      JobExecutionMessage(Instant.now(), JobExecutionMessage.Level.ERROR, "foobar"))

    assertEquals(1, getRepository().findOne(JobExecutionId("foo"))?.messages?.size)

    getRepository().appendMessage(JobExecutionId("foo"),
      JobExecutionMessage(Instant.now(), JobExecutionMessage.Level.ERROR, "foobar"))

    assertEquals(2, getRepository().findOne(JobExecutionId("foo"))?.messages?.size)
  }

  @Test
  fun `ensure updateLastUpdated works`() {
    val jobExecutionFoo = createJobExecution(lastUpdated = Instant.now())
    getRepository().save(jobExecutionFoo)

    val newInstant = Instant.now().epochSecond + 1
    getRepository().updateLastUpdated(JobExecutionId("foo"), Instant.ofEpochSecond(newInstant))

    assertEquals(Instant.ofEpochSecond(newInstant), getRepository().findOne(JobExecutionId("foo"))?.lastUpdated)
  }

  abstract fun getRepository(): JobExecutionRepository

  private fun createJobExecution(
    id: JobExecutionId = JobExecutionId("foo"),
    jobId: JobId = JobId("bar"),
    stopped: Instant? = null,
    messages: List<JobExecutionMessage> = emptyList(),
    hostname: String = "foobar",
    lastUpdated: Instant = Instant.now()
  ) =
    JobExecution(id, jobId, OK, Instant.now(), stopped, messages, hostname, lastUpdated)
}
