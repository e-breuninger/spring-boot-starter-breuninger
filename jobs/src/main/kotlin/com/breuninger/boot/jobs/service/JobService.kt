package com.breuninger.boot.jobs.service

import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobBlockedException
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.domain.JobMutexGroup
import com.breuninger.boot.jobs.repository.JobRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(prefix = "breuni.jobs", name = ["enabled"], havingValue = "true")
class JobService(private val jobRepository: JobRepository, private val mutexGroups: Set<JobMutexGroup>) {

  companion object {

    val LOG: Logger = LoggerFactory.getLogger(JobService::class.java)
  }

  fun create(job: Job) = jobRepository.insert(job)

  @Throws(JobBlockedException::class)
  fun acquireRunLock(jobId: JobId, jobExecutionId: JobExecutionId): Job {
    jobRepository.acquireRunLock(jobId, jobExecutionId)?.let { job ->
      when {
        job.disabled -> {
          releaseRunLock(jobId, jobExecutionId)
          throw JobBlockedException("JobRunnable '$jobId' is currently disabled")
        }
        else -> jobRepository.findRunning(findMutexJobs(jobId))?.let {
          releaseRunLock(jobId, jobExecutionId)
          throw JobBlockedException("JobRunnable '$jobId' blocked by currently running job '$it'")
        }
      }
      return job
    } ?: throw JobBlockedException("JobRunnable '$jobId' is already running")
  }

  fun releaseRunLock(jobId: JobId, jobExecutionId: JobExecutionId) {
    LOG.info("Releasing runLock of $jobId")
    jobRepository.releaseRunLock(jobId, jobExecutionId)
  }

  fun findState(jobId: JobId, key: String) = jobRepository.findState(jobId, key)

  fun updateState(jobId: JobId, key: String, value: String?) = jobRepository.updateState(jobId, key, value)

  private fun findMutexJobs(jobId: JobId) = mutexGroups
    .asSequence()
    .map { it.jobIds }
    .filter { it.contains(jobId) }
    .flatten()
    .filter { it != jobId }
    .toSet()
}
