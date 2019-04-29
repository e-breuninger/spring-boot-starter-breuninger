package com.breuninger.boot.jobs.actuator.health

import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobId
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Component
class JobHealthIndicator: HealthIndicator  {

  private val jobExecutionStatusMap: ConcurrentMap<JobId, JobExecution.Status> = ConcurrentHashMap()

  override fun health(): Health {
    var healthy: Boolean = true
    var failedExecutions: Int = 0
    jobExecutionStatusMap.forEach{(jobId, status) -> if(status == JobExecution.Status.DEAD || status == JobExecution.Status.ERROR) {
      healthy = false
      failedExecutions++
    }}

    if(healthy)
      return Health.up().build()

    return Health.down().withDetail("Failed JobExecutions", failedExecutions).build()
  }

  fun setJobExecutionStatus(jobId: JobId, status: JobExecution.Status){
    jobExecutionStatusMap.set(jobId,status)
  }
}
