package com.breuninger.boot.jobs.domain

import com.breuninger.boot.jobs.domain.JobExecution.Status.OK
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.net.InetAddress
import java.time.Duration
import java.time.Instant
import java.time.Instant.now

// TODO(BS): add tests for runtime
@Document(collection = "jobExecutions")
data class JobExecution(
  @Id val id: JobExecutionId,
  val jobId: JobId,
  val status: Status,
  val started: Instant,
  val stopped: Instant?,
  val runtime: Duration?,
  val messages: List<JobExecutionMessage>,
  val hostname: String,
  val lastUpdated: Instant
) {

  private constructor(jobExecutionId: JobExecutionId, jobId: JobId, started: Instant) :
    this(jobExecutionId,
      jobId,
      OK,
      started,
      null,
      null,
      emptyList(),
      InetAddress.getLocalHost().hostAddress,
      started)

  constructor(jobExecutionId: JobExecutionId, jobId: JobId) : this(jobExecutionId, jobId, now())

  fun hasStopped() = stopped != null

  fun hasNotStopped() = !hasStopped()

  enum class Status {
    OK, SKIPPED, ERROR, DEAD
  }
}
