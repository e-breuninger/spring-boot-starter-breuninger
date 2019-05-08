package com.breuninger.boot.jobs.repository

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isLessThanOrEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobExecution.Status.DEAD
import com.breuninger.boot.jobs.domain.JobExecution.Status.ERROR
import com.breuninger.boot.jobs.domain.JobExecution.Status.OK
import com.breuninger.boot.jobs.domain.JobExecution.Status.SKIPPED
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobExecutionMessage
import com.breuninger.boot.jobs.domain.JobExecutionMessage.Level
import com.breuninger.boot.jobs.domain.JobId
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

    assertThat(getRepository().findOne(JobExecutionId("foo"))).isEqualTo(jobExecutionFoo)
    assertThat(getRepository().findOne(JobExecutionId("bar"))).isEqualTo(jobExecutionBar)
    assertThat(getRepository().findOne(JobExecutionId("bat"))).isEqualTo(jobExecutionBat)
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

    assertThat(jobExecutions.size).isEqualTo(100)
    var lastInstant: Long = Long.MAX_VALUE
    for (jobExecution in jobExecutions) {
      assertThat(jobExecution.lastUpdated.epochSecond).isLessThanOrEqualTo(lastInstant)
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

    assertThat(jobExecutions.size).isEqualTo(100)
    var lastInstant: Long = Long.MAX_VALUE
    for (jobExecution in jobExecutions) {
      assertThat(jobExecution.lastUpdated.epochSecond).isLessThanOrEqualTo(lastInstant)
      assertThat(jobExecution.jobId).isEqualTo(JobId("foo"))
      lastInstant = jobExecution.lastUpdated.epochSecond
    }
  }

  @Test
  fun `ensure finding all ignoring messages works`() {
    for (i in 1..100) {
      getRepository().save(createJobExecution(
        id = JobExecutionId(i.toString()),
        messages = listOf(JobExecutionMessage(Instant.now(), Level.ERROR, "foo bar"))))
    }

    val jobExecutions = getRepository().findAllIgnoreMessages()

    assertThat(jobExecutions.size).isEqualTo(100)
    for (jobExecution in jobExecutions) {
      assertThat(jobExecution.messages).isEmpty()
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

    assertThat(getRepository().findOne(JobExecutionId("foo"))).isNull()
    assertThat(getRepository().findOne(JobExecutionId("bar"))).isNull()
    assertThat(getRepository().findOne(JobExecutionId("bat"))).isNull()
  }

  @Test
  fun `ensure stop works`() {
    val jobExecutionFoo = createJobExecution()
    getRepository().save(jobExecutionFoo)

    assertThat(getRepository().findOne(JobExecutionId("foo"))!!.stopped).isNull()

    getRepository().stop(JobExecutionId("foo"), Instant.now())

    assertThat(getRepository().findOne(JobExecutionId("foo"))!!.stopped).isNotNull()
  }

  @Test
  fun `ensure updateStatus works`() {
    val jobExecutionFoo = createJobExecution()
    getRepository().save(jobExecutionFoo)

    assertThat(getRepository().findOne(JobExecutionId("foo"))!!.status).isEqualTo(OK)

    getRepository().updateStatus(JobExecutionId("foo"), DEAD)
    assertThat(getRepository().findOne(JobExecutionId("foo"))!!.status).isEqualTo(DEAD)

    getRepository().updateStatus(JobExecutionId("foo"), ERROR)
    assertThat(getRepository().findOne(JobExecutionId("foo"))!!.status).isEqualTo(ERROR)

    getRepository().updateStatus(JobExecutionId("foo"), SKIPPED)
    assertThat(getRepository().findOne(JobExecutionId("foo"))!!.status).isEqualTo(SKIPPED)

    getRepository().updateStatus(JobExecutionId("foo"), OK)
    assertThat(getRepository().findOne(JobExecutionId("foo"))!!.status).isEqualTo(OK)
  }

  @Test
  fun `ensure appendMessage works`() {
    val jobExecutionFoo = createJobExecution()
    getRepository().save(jobExecutionFoo)

    assertThat(jobExecutionFoo.messages).isEmpty()

    getRepository().appendMessage(JobExecutionId("foo"),
      JobExecutionMessage(Instant.now(), Level.ERROR, "foobar"))

    assertThat(getRepository().findOne(JobExecutionId("foo"))!!.messages.size).isEqualTo(1)

    getRepository().appendMessage(JobExecutionId("foo"),
      JobExecutionMessage(Instant.now(), Level.ERROR, "foobar"))

    assertThat(getRepository().findOne(JobExecutionId("foo"))!!.messages.size).isEqualTo(2)
  }

  @Test
  fun `ensure updateLastUpdated works`() {
    val jobExecutionFoo = createJobExecution(lastUpdated = Instant.now())
    getRepository().save(jobExecutionFoo)

    val newInstant = Instant.now().epochSecond + 1
    getRepository().updateLastUpdated(JobExecutionId("foo"), Instant.ofEpochSecond(newInstant))

    assertThat(getRepository().findOne(JobExecutionId("foo"))?.lastUpdated).isEqualTo(Instant.ofEpochSecond(newInstant))
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
