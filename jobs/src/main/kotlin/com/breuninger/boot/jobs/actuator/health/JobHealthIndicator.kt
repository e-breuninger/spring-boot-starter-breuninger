package com.breuninger.boot.jobs.actuator.health

import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobId
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Component
class JobHealthIndicator : HealthIndicator {

  private val jobExecutionStatus: ConcurrentMap<JobId, JobExecution.Status> = ConcurrentHashMap()

  override fun health(): Health {
    val health = Health.unknown().withDetails(jobExecutionStatus.map { toDetails(it.key, it.value) }.toMap<String, Health>())
    return if (anyJobExecutionIsDown()) {
      health.down().build()
    } else {
      health.up().build()
    }
  }

  private fun toDetails(jobId: JobId, status: JobExecution.Status) = if (isDown(status)) {
    jobId.value to Health.down().build()
  } else {
    jobId.value to Health.up().build()
  }

  private fun anyJobExecutionIsDown() = jobExecutionStatus.any {
    isDown(it.value)
  }

  private fun isDown(status: JobExecution.Status) = status == JobExecution.Status.DEAD || status == JobExecution.Status.ERROR

  fun setJobExecutionStatus(jobId: JobId, status: JobExecution.Status) {
    jobExecutionStatus[jobId] = status
  }
}
