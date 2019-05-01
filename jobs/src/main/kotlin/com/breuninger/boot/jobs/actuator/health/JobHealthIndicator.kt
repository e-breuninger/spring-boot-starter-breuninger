package com.breuninger.boot.jobs.actuator.health

import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobId
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

// TODO(BS): check this class
@Component
class JobHealthIndicator : HealthIndicator {

  private val jobExecutionStatus: ConcurrentMap<JobId, JobExecution.Status> = ConcurrentHashMap()

  override fun health(): Health {
    var healthy = true
    val map: MutableMap<String, Health> = HashMap()
    jobExecutionStatus.forEach { (jobId, status) ->
      if (status == JobExecution.Status.DEAD || status == JobExecution.Status.ERROR) {
        map[jobId.value] = Health.down().build()
        healthy = false
      } else {
        map[jobId.value] = Health.up().build()
      }
    }
    return if (healthy) {
      Health.up().withDetails(map).build()
    } else {
      Health.down().withDetails(map).build()
    }
  }

  fun setJobExecutionStatus(jobId: JobId, status: JobExecution.Status) {
    jobExecutionStatus[jobId] = status
  }
}
