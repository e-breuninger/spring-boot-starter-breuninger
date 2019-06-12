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

  companion object {

    private const val WARN = "WARN"
  }

  private val jobExecutionStatus: ConcurrentMap<JobId, JobExecution.Status> = ConcurrentHashMap()

  override fun health(): Health {
    val health = Health.unknown().withDetails(jobExecutionStatus.map { toHealth(it.key, it.value) }.toMap<String, Health>())
    return if (anyJobExecutionIsDown()) {
      health.status(WARN).build()
    } else {
      health.up().build()
    }
  }

  private fun toHealth(jobId: JobId, status: JobExecution.Status) = if (isWarn(status)) {
    jobId.value to Health.status(WARN).build()
  } else {
    jobId.value to Health.up().build()
  }

  private fun anyJobExecutionIsDown() = jobExecutionStatus.any {
    isWarn(it.value)
  }

  private fun isWarn(status: JobExecution.Status) = status == JobExecution.Status.DEAD || status == JobExecution.Status.ERROR

  fun setJobExecutionStatus(jobId: JobId, status: JobExecution.Status) {
    jobExecutionStatus[jobId] = status
  }
}
