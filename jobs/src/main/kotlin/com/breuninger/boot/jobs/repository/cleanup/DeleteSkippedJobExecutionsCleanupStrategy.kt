package com.breuninger.boot.jobs.repository.cleanup

import com.breuninger.boot.jobs.autoconfigure.JobsProperties
import com.breuninger.boot.jobs.domain.JobExecution.Status.SKIPPED
import com.breuninger.boot.jobs.service.JobExecutionService
import io.micrometer.core.annotation.Timed
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DeleteSkippedJobExecutionsCleanupStrategy(
  private val jobExecutionService: JobExecutionService,
  private val jobsProperties: JobsProperties
) : JobExecutionCleanupStrategy {

  companion object {

    private const val DELETE_SKIPPED_JOB_EXECUTIONS_CLEANUP_INTERVAL = 10L * 60L * 1000L
  }

  @Timed("com.breuninger.boot.jobs.repository.cleanup.DeleteSkippedJobExecutionsCleanupStrategy.cleanUp", longTask = true)
  @Scheduled(fixedRate = DELETE_SKIPPED_JOB_EXECUTIONS_CLEANUP_INTERVAL)
  override fun cleanUp() {
    jobExecutionService.findAllIgnoreMessages()
      .sortedByDescending { it.started }
      .groupBy { it.jobId }
      .flatMap { jobToJobExecutions ->
        jobToJobExecutions.value
          .filter { it.hasStopped() && it.status == SKIPPED }
          .drop(jobsProperties.cleanup.numberOfSkippedJobExecutionsToKeep)
      }
      .forEach { jobExecutionService.remove(it) }
  }
}
