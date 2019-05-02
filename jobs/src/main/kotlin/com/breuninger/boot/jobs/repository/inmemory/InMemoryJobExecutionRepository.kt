package com.breuninger.boot.jobs.repository.inmemory

import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobExecution.Status
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobExecutionMessage
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.repository.JobExecutionRepository
import java.time.Instant
import java.time.Instant.now
import java.util.concurrent.ConcurrentHashMap

// TODO(BS): sort methods
class InMemoryJobExecutionRepository : JobExecutionRepository {

  override fun findAll(jobExecutionId: JobExecutionId): List<JobExecution> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  private val jobExecutions = ConcurrentHashMap<JobExecutionId, JobExecution>()

  override fun findOne(jobExecutionId: JobExecutionId) = jobExecutions[jobExecutionId]

  override fun findAllWithoutMessages() = jobExecutions.values
    .sortedByDescending { it.started }
    .map { it.copy(messages = emptyList()) }

  // TODO(BS): secure that this is not happening to the next repository
  // TODO(BS): sort, reverse
  // TODO(BS): take 100
  // TODO(BS): need to add jobId filter if not null
  override fun findAll(jobId: JobId?): List<JobExecution> = ArrayList<JobExecution>(jobExecutions.values)

  override fun save(jobExecution: JobExecution): JobExecution {
    jobExecutions[jobExecution.id] = jobExecution
    return jobExecution
  }

  override fun remove(jobExecution: JobExecution) {
    jobExecutions.remove(jobExecution.id)
  }

  override fun stop(jobExecutionId: JobExecutionId) = findOne(jobExecutionId)?.let {
    val stopped = now()
    jobExecutions.replace(jobExecutionId, it.copy(stopped = stopped, lastUpdated = stopped))
    Unit
  }

  override fun updateStatus(jobExecutionId: JobExecutionId, status: Status) = findOne(jobExecutionId)?.let {
    jobExecutions.replace(jobExecutionId, it.copy(status = status))
    Unit
  }

  override fun appendMessage(jobExecutionId: JobExecutionId, message: JobExecutionMessage) = findOne(jobExecutionId)?.let {
    val messages = ArrayList(it.messages)
    messages.add(message)
    jobExecutions.replace(jobExecutionId, it.copy(lastUpdated = message.timestamp, messages = messages))
    Unit
  }

  override fun updateLastUpdated(jobExecutionId: JobExecutionId, lastUpdated: Instant) = findOne(jobExecutionId)?.let {
    jobExecutions.replace(jobExecutionId, it.copy(lastUpdated = lastUpdated))
    Unit
  }
}
