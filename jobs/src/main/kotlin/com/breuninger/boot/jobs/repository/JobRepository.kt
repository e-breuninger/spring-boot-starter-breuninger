package com.breuninger.boot.jobs.repository

import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobBlockedException
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobId

interface JobRepository {

  fun findOne(jobId: JobId): Job?

  fun findRunning(jobIds: Set<JobId>): Job?

  fun insert(job: Job)

  fun acquireRunLock(jobId: JobId, jobExecutionId: JobExecutionId): Job?

  @Throws(JobBlockedException::class)
  fun releaseRunLock(jobId: JobId, jobExecutionId: JobExecutionId): Unit?

  fun findState(jobId: JobId, key: String): String?

  fun updateState(jobId: JobId, key: String, value: String?): Unit?

  fun findAllJobs(): List<Job>

  fun disable(jobId: JobId, disableComment: String)

  fun enable(jobId: JobId)
}