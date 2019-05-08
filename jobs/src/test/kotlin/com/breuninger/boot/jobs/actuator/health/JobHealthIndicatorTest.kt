package com.breuninger.boot.jobs.actuator.health

import com.breuninger.boot.jobs.domain.JobExecution.Status.DEAD
import com.breuninger.boot.jobs.domain.JobExecution.Status.ERROR
import com.breuninger.boot.jobs.domain.JobExecution.Status.OK
import com.breuninger.boot.jobs.domain.JobExecution.Status.SKIPPED
import com.breuninger.boot.jobs.domain.JobId
// TODO(KA): switch for assertk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.Status.DOWN
import org.springframework.boot.actuate.health.Status.UP

internal class JobHealthIndicatorTest {

  private val healthIndicator = JobHealthIndicator()

  @Test
  fun `ensure health is up when every execution is up`() {
    healthIndicator.setJobExecutionStatus(JobId("test"), OK)
    healthIndicator.setJobExecutionStatus(JobId("foo"), OK)
    healthIndicator.setJobExecutionStatus(JobId("bar"), SKIPPED)

    val actualHealth = healthIndicator.health()

    assertEquals(UP, actualHealth.status)
    assertEquals(UP, (actualHealth.details["test"] as Health).status)
    assertEquals(UP, (actualHealth.details["foo"] as Health).status)
    assertEquals(UP, (actualHealth.details["bar"] as Health).status)
  }

  @Test
  fun `ensure health is down when one execution has an error`() {
    healthIndicator.setJobExecutionStatus(JobId("test"), OK)
    healthIndicator.setJobExecutionStatus(JobId("foo"), OK)
    healthIndicator.setJobExecutionStatus(JobId("bar"), ERROR)

    val actualHealth = healthIndicator.health()

    assertEquals(DOWN, actualHealth.status)
    assertEquals(UP, (actualHealth.details["test"] as Health).status)
    assertEquals(UP, (actualHealth.details["foo"] as Health).status)
    assertEquals(DOWN, (actualHealth.details["bar"] as Health).status)
  }

  @Test
  fun `ensure health is down when one execution is dead`() {
    healthIndicator.setJobExecutionStatus(JobId("test"), OK)
    healthIndicator.setJobExecutionStatus(JobId("foo"), OK)
    healthIndicator.setJobExecutionStatus(JobId("bar"), DEAD)

    val actualHealth = healthIndicator.health()

    assertEquals(DOWN, actualHealth.status)
    assertEquals(UP, (actualHealth.details["test"] as Health).status)
    assertEquals(UP, (actualHealth.details["foo"] as Health).status)
    assertEquals(DOWN, (actualHealth.details["bar"] as Health).status)
  }

  @Test
  fun `ensure health is down when every execution is down`() {
    healthIndicator.setJobExecutionStatus(JobId("test"), ERROR)
    healthIndicator.setJobExecutionStatus(JobId("foo"), DEAD)
    healthIndicator.setJobExecutionStatus(JobId("bar"), DEAD)

    val actualHealth = healthIndicator.health()

    assertEquals(DOWN, actualHealth.status)
    assertEquals(DOWN, (actualHealth.details["test"] as Health).status)
    assertEquals(DOWN, (actualHealth.details["foo"] as Health).status)
    assertEquals(DOWN, (actualHealth.details["bar"] as Health).status)
  }
}
