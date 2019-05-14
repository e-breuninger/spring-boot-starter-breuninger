package com.breuninger.boot.jobs.repository

import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobExecution.Status
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobExecutionMessage
import com.breuninger.boot.jobs.domain.JobId
import java.time.Instant

interface JobExecutionRepository {

  fun findOne(jobExecutionId: JobExecutionId): JobExecution?

  fun find100DescendingByLastUpdated(jobId: JobId?): List<JobExecution>

  fun findAllIgnoreMessages(): List<JobExecution>

  fun save(jobExecution: JobExecution): JobExecution

  fun updateStatus(jobExecutionId: JobExecutionId, status: Status): Unit?

  fun updateLastUpdated(jobExecutionId: JobExecutionId, lastUpdated: Instant): Unit?

  fun appendMessage(jobExecutionId: JobExecutionId, message: JobExecutionMessage): Unit?

  fun stop(jobExecutionId: JobExecutionId, stopped: Instant): Unit?

  fun remove(jobExecution: JobExecution)

  fun drop()
}
