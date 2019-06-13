package com.breuninger.boot.jobs.repository

import com.breuninger.boot.jobs.JobExecutor
import com.breuninger.boot.jobs.domain.JobId
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Component
class JobExecutorRegistry {

  private val jobExecutorMap: ConcurrentMap<JobId, JobExecutor> = ConcurrentHashMap()

  fun register(jobId: JobId, jobExecutor: JobExecutor) {
    jobExecutorMap[jobId] = jobExecutor
  }

  fun findOne(jobId: JobId) = jobExecutorMap[jobId]
}
