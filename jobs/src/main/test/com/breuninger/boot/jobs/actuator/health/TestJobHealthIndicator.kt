package com.breuninger.boot.jobs.actuator.health

import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.actuate.health.Status

class TestJobHealthIndicator {
  private val healthIndicator = JobHealthIndicator()

  @Test
  fun `ensure health is up when every execution is up` () {
    healthIndicator.setJobExecutionStatus(JobId("test"), JobExecution.Status.OK)
    healthIndicator.setJobExecutionStatus(JobId("foo"), JobExecution.Status.OK)
    healthIndicator.setJobExecutionStatus(JobId("bar"), JobExecution.Status.OK)

    val actualHealth = healthIndicator.health()

    Assertions.assertEquals(Status.UP, actualHealth.status)
  }

  @Test
  fun `ensure health is down when one execution has an error` () {
    healthIndicator.setJobExecutionStatus(JobId("test"), JobExecution.Status.OK)
    healthIndicator.setJobExecutionStatus(JobId("foo"), JobExecution.Status.OK)
    healthIndicator.setJobExecutionStatus(JobId("bar"), JobExecution.Status.ERROR)

    val actualHealth = healthIndicator.health()

    Assertions.assertEquals(Status.DOWN, actualHealth.status)
  }

  @Test
  fun `ensure health is down when one execution is dead` () {
    healthIndicator.setJobExecutionStatus(JobId("test"), JobExecution.Status.OK)
    healthIndicator.setJobExecutionStatus(JobId("foo"), JobExecution.Status.OK)
    healthIndicator.setJobExecutionStatus(JobId("bar"), JobExecution.Status.DEAD)

    val actualHealth = healthIndicator.health()

    Assertions.assertEquals(Status.DOWN, actualHealth.status)
  }

  @Test
  fun `ensure health is down when every execution is down` () {
    healthIndicator.setJobExecutionStatus(JobId("test"), JobExecution.Status.ERROR)
    healthIndicator.setJobExecutionStatus(JobId("foo"), JobExecution.Status.DEAD)
    healthIndicator.setJobExecutionStatus(JobId("bar"), JobExecution.Status.DEAD)

    val actualHealth = healthIndicator.health()

    Assertions.assertEquals(Status.DOWN, actualHealth.status)
  }
}
