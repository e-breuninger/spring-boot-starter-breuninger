package com.breuninger.boot.jobs.repository.inmemory

import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobBlockedException
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.repository.JobRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

class InMemoryJobRepository : JobRepository {

  companion object {

    val LOG: Logger = LoggerFactory.getLogger(InMemoryJobRepository::class.java)
  }

  private val jobs = ConcurrentHashMap<JobId, Job>()

  override fun findOne(jobId: JobId): Job? = jobs[jobId]

  override fun findOneRunning(jobIds: Set<JobId>) = jobs.values.find { jobIds.contains(it.id) && it.isRunning() }

  override fun findAll(): List<Job> = ArrayList<Job>(jobs.values)

  override fun create(job: Job) = findOne(job.id)?.let { LOG.info("Job already created") } ?: run { jobs[job.id] = job }

  override fun updateDisableState(jobId: JobId, job: Job) = jobs[jobId]?.let {
    jobs.replace(jobId, it.copy(disabled = job.disabled, disableComment = job.disableComment))
  }

  override fun acquireRunLock(jobId: JobId, jobExecutionId: JobExecutionId) = findOne(jobId)?.let {
    if (it.isRunning()) null else jobs.replace(jobId, it.copy(runningJobExecutionId = jobExecutionId))
  }

  @Throws(JobBlockedException::class)
  override fun releaseRunLock(jobId: JobId, jobExecutionId: JobExecutionId) = findOne(jobId)?.let {
    if (it.runningJobExecutionId == jobExecutionId) {
      jobs.replace(jobId, it.copy(runningJobExecutionId = null))
      Unit
    } else {
      throw JobBlockedException(
        "Tried to release runLock of $jobId but different execution ${it.runningJobExecutionId} was running")
    }
  }

  override fun findState(jobId: JobId, key: String) = findOne(jobId)?.let {
    it.state?.get(key)
  }

  // TODO(BS): directly updateDisableState keys in map
  override fun updateState(jobId: JobId, key: String, value: String?) = findOne(jobId)?.let {
    val state = HashMap(it.state)
    value?.run { state[key] = value } ?: state.remove(key)
    jobs.replace(jobId, it.copy(state = state))
    Unit
  }

  override fun remove(job: Job) {
    jobs.remove(job.id)
  }

  override fun drop() = jobs.clear()
}
