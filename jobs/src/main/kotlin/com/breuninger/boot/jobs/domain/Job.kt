package com.breuninger.boot.jobs.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "jobs")
data class Job(
  @Id val id: JobId,
  val runningJobExecutionId: JobExecutionId?,
  val disabled: Boolean,
  val disableComment: String,
  val state: Map<String, String>
) {

  constructor(jobId: JobId) : this(jobId, null, false, "", emptyMap())

  fun isRunning() = runningJobExecutionId != null

  fun isNotRunning() = !isRunning()
}
