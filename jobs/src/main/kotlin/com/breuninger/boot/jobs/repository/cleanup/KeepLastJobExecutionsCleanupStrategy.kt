package com.breuninger.boot.jobs.repository.cleanup

import com.breuninger.boot.jobs.autoconfigure.JobsProperties
import com.breuninger.boot.jobs.domain.JobExecution.Status.OK
import com.breuninger.boot.jobs.service.JobExecutionService
import io.micrometer.core.annotation.Timed
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class KeepLastJobExecutionsCleanupStrategy(
  private val jobExecutionService: JobExecutionService,
  private val jobsProperties: JobsProperties
) : JobExecutionCleanupStrategy {

  companion object {

    private const val KEEP_LAST_JOB_EXECUTIONS_CLEANUP_INTERVAL = 10L * 60L * 1000L
  }

  @Timed("com.breuninger.boot.jobs.repository.cleanup.KeepLastJobExecutionsCleanupStrategy.cleanUp", longTask = true)
  @Scheduled(fixedRate = KEEP_LAST_JOB_EXECUTIONS_CLEANUP_INTERVAL)
  override fun cleanUp() {
    jobExecutionService.findAllIgnoreMessages()
      .sortedByDescending { it.started }
      .groupBy { it.jobId }
      .flatMap { jobToJobExecutions ->
        val lastOkExecution = jobToJobExecutions.value.find { it.hasStopped() && it.status == OK }
        jobToJobExecutions.value
          .filter { it.hasStopped() }
          .drop(jobsProperties.cleanup.numberOfJobExecutionsToKeep)
          .filter { it != lastOkExecution }
      }
      .forEach { jobExecutionService.remove(it) }
  }
}
