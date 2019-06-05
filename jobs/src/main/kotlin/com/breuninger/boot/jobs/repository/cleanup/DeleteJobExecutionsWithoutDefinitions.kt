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
) : JobExecutionCleanupStrategy {

  companion object {

    private const val DELETE_JOB_EXECUTIONS_WITHOUT_DEFINITIONS_CLEANUP_INTERVAL = 10L * 60L * 1000L
  }

  @Timed("com.breuninger.boot.jobs.repository.cleanup.DeleteJobExecutionsWithoutDefinitions.cleanUp", longTask = true)
  @Scheduled(fixedRate = DELETE_JOB_EXECUTIONS_WITHOUT_DEFINITIONS_CLEANUP_INTERVAL)
  override fun cleanUp() {
    val jobIdsWithDefinitions = jobRunnables.map { it.definition().jobId }.toList()
    jobExecutionService.findAllIgnoreMessages().forEach {
      if (!jobIdsWithDefinitions.contains(it.jobId))
        jobExecutionService.remove(it)
    }
  }
}
