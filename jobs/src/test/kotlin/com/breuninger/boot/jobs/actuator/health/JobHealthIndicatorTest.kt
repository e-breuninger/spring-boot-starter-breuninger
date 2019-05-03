package com.breuninger.boot.jobs.actuator.health

import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.Status

internal class JobHealthIndicatorTest {

  private val healthIndicator = JobHealthIndicator()

  @Test
  fun `ensure health is up when every execution is up`() {
    healthIndicator.setJobExecutionStatus(JobId("test"), JobExecution.Status.OK)
    healthIndicator.setJobExecutionStatus(JobId("foo"), JobExecution.Status.OK)
    healthIndicator.setJobExecutionStatus(JobId("bar"), JobExecution.Status.SKIPPED)

    val actualHealth = healthIndicator.health()

    assertEquals(Status.UP, actualHealth.status)
    assertEquals(Status.UP, (actualHealth.details["test"] as Health).status)
    assertEquals(Status.UP, (actualHealth.details["foo"] as Health).status)
    assertEquals(Status.UP, (actualHealth.details["bar"] as Health).status)
  }

  @Test
  fun `ensure health is down when one execution has an error`() {
    healthIndicator.setJobExecutionStatus(JobId("test"), JobExecution.Status.OK)
    healthIndicator.setJobExecutionStatus(JobId("foo"), JobExecution.Status.OK)
    healthIndicator.setJobExecutionStatus(JobId("bar"), JobExecution.Status.ERROR)

    val actualHealth = healthIndicator.health()

    assertEquals(Status.DOWN, actualHealth.status)
    assertEquals(Status.UP, (actualHealth.details["test"] as Health).status)
    assertEquals(Status.UP, (actualHealth.details["foo"] as Health).status)
    assertEquals(Status.DOWN, (actualHealth.details["bar"] as Health).status)
  }

  @Test
  fun `ensure health is down when one execution is dead`() {
    healthIndicator.setJobExecutionStatus(JobId("test"), JobExecution.Status.OK)
    healthIndicator.setJobExecutionStatus(JobId("foo"), JobExecution.Status.OK)
    healthIndicator.setJobExecutionStatus(JobId("bar"), JobExecution.Status.DEAD)

    val actualHealth = healthIndicator.health()

    assertEquals(Status.DOWN, actualHealth.status)
    assertEquals(Status.UP, (actualHealth.details["test"] as Health).status)
    assertEquals(Status.UP, (actualHealth.details["foo"] as Health).status)
    assertEquals(Status.DOWN, (actualHealth.details["bar"] as Health).status)
  }

  @Test
  fun `ensure health is down when every execution is down`() {
    healthIndicator.setJobExecutionStatus(JobId("test"), JobExecution.Status.ERROR)
    healthIndicator.setJobExecutionStatus(JobId("foo"), JobExecution.Status.DEAD)
    healthIndicator.setJobExecutionStatus(JobId("bar"), JobExecution.Status.DEAD)

    val actualHealth = healthIndicator.health()

    assertEquals(Status.DOWN, actualHealth.status)
    assertEquals(Status.DOWN, (actualHealth.details["test"] as Health).status)
    assertEquals(Status.DOWN, (actualHealth.details["foo"] as Health).status)
    assertEquals(Status.DOWN, (actualHealth.details["bar"] as Health).status)
  }
}
