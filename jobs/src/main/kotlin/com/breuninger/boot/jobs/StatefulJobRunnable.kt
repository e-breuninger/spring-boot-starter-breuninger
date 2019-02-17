package com.breuninger.boot.jobs

import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.service.JobService
import java.time.Instant
import java.time.Instant.ofEpochMilli

abstract class StatefulJobRunnable(val jobId: JobId, private val jobService: JobService) : JobRunnable {

  private fun findStateAsString(key: String): String? {
    return jobService.findState(jobId, key)
  }

  private fun updateState(key: String, value: String) {
    jobService.updateState(jobId, key, value)
  }

  private fun deleteState(key: String) {
    jobService.updateState(jobId, key, null)
  }

  private fun findStateAsInt(key: String): Int? = findStateAsString(key)?.toIntOrNull()

  private fun updateState(key: String, value: Int) = updateState(key, value.toString())

  private fun findStateAsLong(key: String): Long? = findStateAsString(key)?.toLongOrNull()

  private fun updateState(key: String, value: Long) = updateState(key, value.toString())

  private fun findStateAsInstant(key: String): Instant? = findStateAsLong(key)?.let { ofEpochMilli(it) }

  private fun updateState(key: String, value: Instant) = updateState(key, value.toEpochMilli())
}
