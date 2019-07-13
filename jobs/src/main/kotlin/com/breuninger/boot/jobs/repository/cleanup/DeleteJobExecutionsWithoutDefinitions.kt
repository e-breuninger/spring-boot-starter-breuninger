package com.breuninger.boot.jobs.repository.cleanup

import com.breuninger.boot.jobs.JobRunnable
import com.breuninger.boot.jobs.service.JobExecutionService
import io.micrometer.core.annotation.Timed
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DeleteJobExecutionsWithoutDefinitions(
  private val jobExecutionService: JobExecutionService,
  private val jobRunnables: List<JobRunnable>
) {

  companion object {

    private const val DELETE_JOB_EXECUTIONS_WITHOUT_DEFINITIONS_CLEANUP_FIXED_RATE = 10L * 1000L
  }

  @Timed("com.breuninger.boot.jobs.repository.cleanup.DeleteJobExecutionsWithoutDefinitions.cleanUp", longTask = true)
  @Scheduled(initialDelay = 0, fixedRate = DELETE_JOB_EXECUTIONS_WITHOUT_DEFINITIONS_CLEANUP_FIXED_RATE)
  fun cleanUp() {
    jobExecutionService.findAllIgnoreMessages().forEach {
      if (!jobRunnables.map { it.definition().jobId }.toList().contains(it.jobId)) {
        jobExecutionService.remove(it)
      }
    }
  }
}
