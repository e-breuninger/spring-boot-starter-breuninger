package com.breuninger.boot.jobs.repository.inmemory

import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobExecution.Status
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobExecutionMessage
import com.breuninger.boot.jobs.repository.JobExecutionRepository
import java.time.Instant
import java.time.Instant.now
import java.util.concurrent.ConcurrentHashMap

class InMemoryJobExecutionRepository : JobExecutionRepository {

  private val jobExecutions = ConcurrentHashMap<JobExecutionId, JobExecution>()

  override fun findOne(jobExecutionId: JobExecutionId) = jobExecutions[jobExecutionId]

  override fun findAllWithoutMessages() = jobExecutions.values
    .sortedByDescending { it.started }
    .map { it.copy(messages = emptyList()) }

  override fun findAll(): List<JobExecution> = ArrayList<JobExecution>(jobExecutions.values)

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