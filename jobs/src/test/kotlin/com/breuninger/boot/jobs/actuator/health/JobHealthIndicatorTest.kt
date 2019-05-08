package com.breuninger.boot.jobs.actuator.health

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.breuninger.boot.jobs.domain.JobExecution.Status.*
import com.breuninger.boot.jobs.domain.JobId
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

    assertThat(actualHealth.status).isEqualTo(UP)
    assertThat((actualHealth.details["test"] as Health).status).isEqualTo(UP)
    assertThat((actualHealth.details["foo"] as Health).status).isEqualTo(UP)
    assertThat((actualHealth.details["bar"] as Health).status).isEqualTo(UP)
  }

  @Test
  fun `ensure health is down when one execution has an error`() {
    healthIndicator.setJobExecutionStatus(JobId("test"), OK)
    healthIndicator.setJobExecutionStatus(JobId("foo"), OK)
    healthIndicator.setJobExecutionStatus(JobId("bar"), ERROR)

    val actualHealth = healthIndicator.health()

    assertThat(actualHealth.status).isEqualTo(DOWN)
    assertThat((actualHealth.details["test"] as Health).status).isEqualTo(UP)
    assertThat((actualHealth.details["foo"] as Health).status).isEqualTo(UP)
    assertThat((actualHealth.details["bar"] as Health).status).isEqualTo(DOWN)
  }

  @Test
  fun `ensure health is down when one execution is dead`() {
    healthIndicator.setJobExecutionStatus(JobId("test"), OK)
    healthIndicator.setJobExecutionStatus(JobId("foo"), OK)
    healthIndicator.setJobExecutionStatus(JobId("bar"), DEAD)

    val actualHealth = healthIndicator.health()

    assertThat(actualHealth.status).isEqualTo(DOWN)
    assertThat((actualHealth.details["test"] as Health).status).isEqualTo(UP)
    assertThat((actualHealth.details["foo"] as Health).status).isEqualTo(UP)
    assertThat((actualHealth.details["bar"] as Health).status).isEqualTo(DOWN)
  }

  @Test
  fun `ensure health is down when every execution is down`() {
    healthIndicator.setJobExecutionStatus(JobId("test"), ERROR)
    healthIndicator.setJobExecutionStatus(JobId("foo"), DEAD)
    healthIndicator.setJobExecutionStatus(JobId("bar"), DEAD)

    val actualHealth = healthIndicator.health()

    assertThat(actualHealth.status).isEqualTo(DOWN)
    assertThat((actualHealth.details["test"] as Health).status).isEqualTo(DOWN)
    assertThat((actualHealth.details["foo"] as Health).status).isEqualTo(DOWN)
    assertThat((actualHealth.details["bar"] as Health).status).isEqualTo(DOWN)
  }
}
