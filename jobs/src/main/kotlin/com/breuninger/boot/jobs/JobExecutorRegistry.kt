package com.breuninger.boot.jobs

import com.breuninger.boot.jobs.domain.JobId
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Component
class JobExecutorRegistry {

  private val jobExecutorMap: ConcurrentMap<JobId, JobExecutor> = ConcurrentHashMap()

  fun register(jobId: JobId,jobExecutor: JobExecutor){
    jobExecutorMap.set(jobId,jobExecutor)
  }

  fun find (jobId: JobId): JobExecutor? {
    return jobExecutorMap.get(jobId)
  }
}
