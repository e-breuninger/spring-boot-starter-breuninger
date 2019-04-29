package com.breuninger.boot.jobs.service

import com.breuninger.boot.jobs.actuator.health.JobHealthIndicator
import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobExecution.Status
import com.breuninger.boot.jobs.domain.JobExecution.Status.*
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobExecutionMessage
import com.breuninger.boot.jobs.domain.JobExecutionMessage.Level
import com.breuninger.boot.jobs.domain.JobExecutionMessage.Level.INFO
import com.breuninger.boot.jobs.domain.JobExecutionMessage.Level.WARNING
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.repository.JobExecutionRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.time.Instant.now

@Service
@ConditionalOnProperty(prefix = "breuni.jobs", name = ["enabled"], havingValue = "true")
class JobExecutionService(
  private val jobService: JobService,
  private val jobExecutionRepository: JobExecutionRepository,
  private val jobHealthIndicator: JobHealthIndicator
) {

  fun findAllWithoutMessages() = jobExecutionRepository.findAllWithoutMessages()

  fun createOrUpdate(jobExecution: JobExecution) = jobExecutionRepository.save(jobExecution)

  fun remove(jobExecution: JobExecution) = jobExecutionRepository.remove(jobExecution)

  fun keepAlive(jobExecutionId: JobExecutionId) = jobExecutionRepository.updateLastUpdated(jobExecutionId, now())

  fun stop(jobId: JobId, jobExecutionId: JobExecutionId) {
    jobExecutionRepository.stop(jobExecutionId)
    jobService.releaseRunLock(jobId, jobExecutionId)
    val jobExecution = findOne(jobExecutionId)
    if(jobExecution != null)
      jobHealthIndicator.setJobExecutionStatus(jobId, jobExecution.status)
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

  fun findAllJobExecutions() = jobExecutionRepository.findAll()

  fun findOne(jobExecutionId: JobExecutionId) = jobExecutionRepository.findOne(jobExecutionId)
}
