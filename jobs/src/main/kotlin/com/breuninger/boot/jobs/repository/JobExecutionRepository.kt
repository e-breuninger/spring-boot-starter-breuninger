package com.breuninger.boot.jobs.repository

import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobExecution.Status
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobExecutionMessage
import java.time.Instant

interface JobExecutionRepository {

  fun findOne(jobExecutionId: JobExecutionId): JobExecution?

  fun findAllWithoutMessages(): List<JobExecution>

  fun save(jobExecution: JobExecution): JobExecution

  fun remove(jobExecution: JobExecution)

  fun stop(jobExecutionId: JobExecutionId): Unit?

  fun updateStatus(jobExecutionId: JobExecutionId, status: Status): Unit?

  fun appendMessage(jobExecutionId: JobExecutionId, message: JobExecutionMessage): Unit?

  fun updateLastUpdated(jobExecutionId: JobExecutionId, lastUpdated: Instant): Unit?
}
