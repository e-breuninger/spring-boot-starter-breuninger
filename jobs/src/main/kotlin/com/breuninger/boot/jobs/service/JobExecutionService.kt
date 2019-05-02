package com.breuninger.boot.jobs.service

import com.breuninger.boot.jobs.actuator.health.JobHealthIndicator
import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobBlockedException
import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobExecution.Status
import com.breuninger.boot.jobs.domain.JobExecution.Status.DEAD
import com.breuninger.boot.jobs.domain.JobExecution.Status.OK
import com.breuninger.boot.jobs.domain.JobExecution.Status.SKIPPED
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobExecutionMessage
import com.breuninger.boot.jobs.domain.JobExecutionMessage.Level
import com.breuninger.boot.jobs.domain.JobExecutionMessage.Level.INFO
import com.breuninger.boot.jobs.domain.JobExecutionMessage.Level.WARNING
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.domain.JobMutexGroup
import com.breuninger.boot.jobs.repository.JobExecutionRepository
import com.breuninger.boot.jobs.repository.JobExecutorRegistry
import com.breuninger.boot.jobs.repository.JobRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.time.Instant.now

// TODO(BS): sort methods
@Service
@ConditionalOnProperty(prefix = "breuni.jobs", name = ["enabled"], havingValue = "true")
class JobExecutionService(
  private val jobExecutionRepository: JobExecutionRepository,
  private val jobRepository: JobRepository,
  private val jobHealthIndicator: JobHealthIndicator,
  private val jobExecutorRegistry: JobExecutorRegistry,
  private val mutexGroups: Set<JobMutexGroup>
) {

  companion object {

    val LOG: Logger = LoggerFactory.getLogger(JobExecutionService::class.java)
  }

  fun findAllWithoutMessages() = jobExecutionRepository.findAllWithoutMessages()

  fun save(jobExecution: JobExecution) = jobExecutionRepository.save(jobExecution)

  fun remove(jobExecution: JobExecution) = jobExecutionRepository.remove(jobExecution)

  fun keepAlive(jobExecutionId: JobExecutionId) = jobExecutionRepository.updateLastUpdated(jobExecutionId, now())

  fun stop(jobId: JobId, jobExecutionId: JobExecutionId) {
    jobExecutionRepository.stop(jobExecutionId)
    releaseRunLock(jobId, jobExecutionId)?.let {
      jobHealthIndicator.setJobExecutionStatus(jobId, it.status)
    }
  }

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

  private fun findMutexJobs(jobId: JobId) = mutexGroups
    .asSequence()
    .map { it.jobIds }
    .filter { it.contains(jobId) }
    .flatten()
    .filter { it != jobId }
    .toSet()

  fun releaseRunLock(jobId: JobId, jobExecutionId: JobExecutionId): JobExecution? {
    LOG.info("Releasing runLock of $jobId")
    return jobRepository.releaseRunLock(jobId, jobExecutionId)?.let { jobExecutionRepository.findOne(jobExecutionId) }
  }

  fun appendMessage(jobExecutionId: JobExecutionId, message: JobExecutionMessage) {
    val status = if (message.level == Level.ERROR) Status.ERROR else null
    appendMessageAndUpdateStatus(jobExecutionId, message, status)
  }

  fun markRestarted(jobExecutionId: JobExecutionId) =
    appendMessageAndUpdateStatus(jobExecutionId, JobExecutionMessage(now(), WARNING, "Restarting job ..."), OK)

  fun markSkipped(jobExecutionId: JobExecutionId) =
    appendMessageAndUpdateStatus(jobExecutionId, JobExecutionMessage(now(), INFO, "Skipped job ..."), SKIPPED)

  fun markDead(jobExecutionId: JobExecutionId) =
    appendMessageAndUpdateStatus(jobExecutionId,
      JobExecutionMessage(now(), WARNING, "Job didn't receive updates for a while, considering it dead"), DEAD)

  private fun appendMessageAndUpdateStatus(jobExecutionId: JobExecutionId, message: JobExecutionMessage, status: Status?) {
    jobExecutionRepository.appendMessage(jobExecutionId, message)
    status?.let { jobExecutionRepository.updateStatus(jobExecutionId, it) }
  }

  fun findAll(jobId: JobId?) = jobExecutionRepository.findAll(jobId)

  fun findOne(jobExecutionId: JobExecutionId) = jobExecutionRepository.findOne(jobExecutionId)

  fun create(jobId: JobId) = jobExecutorRegistry.findOne(jobId)?.let { Thread(it).start() }
}
