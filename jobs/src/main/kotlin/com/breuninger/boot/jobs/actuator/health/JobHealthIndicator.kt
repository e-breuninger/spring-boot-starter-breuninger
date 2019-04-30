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
    var map: MutableMap<String, Health> = HashMap()
    jobExecutionStatusMap.forEach{(jobId, status) -> if(status == JobExecution.Status.DEAD || status == JobExecution.Status.ERROR) {
      map.set(jobId.value, Health.down().build())
      healthy = false
    }else{
      map.set(jobId.value, Health.up().build())
    }}

    if(healthy)
      return Health.up().withDetails(map).build()

    return Health.down().withDetails(map).build()
  }

  fun setJobExecutionStatus(jobId: JobId, status: JobExecution.Status){
    jobExecutionStatusMap.set(jobId,status)
  }
}
